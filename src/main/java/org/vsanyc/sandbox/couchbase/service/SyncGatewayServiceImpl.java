package org.vsanyc.sandbox.couchbase.service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.vsanyc.sandbox.couchbase.entities.BulkOptions;
import org.vsanyc.sandbox.couchbase.entities.SgDoc;
import org.vsanyc.sandbox.couchbase.entities.SgDocBody;

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SyncGatewayServiceImpl implements SyncGatewayService {

    private static final String BULK = "/{0}/_bulk_docs";

    @Value("${sync_gateway.url.template}")
    private String syncGatewayTemplatePath;

    @Override
    public HttpStatus createDocuments(BulkOptions bulkOptions) {
        String path = MessageFormat.format(syncGatewayTemplatePath, bulkOptions.getUsername(),
                        bulkOptions.getPassword())
                + MessageFormat.format(BULK, bulkOptions.getBucket());
        SgDocBody body = createDocs(bulkOptions.getUsername(), bulkOptions.getChannel(),
                bulkOptions.getCount());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createHeaders(bulkOptions.getUsername(), bulkOptions.getPassword());
        HttpEntity<SgDocBody> bodyHttpEntity = new HttpEntity<>(body, headers);
        ResponseEntity responseEntity = restTemplate.postForEntity(path, bodyHttpEntity, String.class);
        HttpStatus status = responseEntity.getStatusCode();
        return status;
    }

    @Override
    public void getAllDocs(String userName, String password, String bucket) {

    }

    private SgDocBody createDocs(String user, String channel, int count) {
        SgDocBody body = new SgDocBody();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date date = new Date();
        String valuePrefix = user + "_" + channel + "_";
        for (int i = 0; i < count; i++) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("channel", channel);
            doc.put("username", user);
            String value = valuePrefix + i + "_" + sdf.format(date);
            doc.put("value", value);
            body.getDocs().add(doc);
        }
        return body;
    }

    private HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
            set("Content-Type", "application/json");
            set("accept", "application/json");
        }};
    }
}
