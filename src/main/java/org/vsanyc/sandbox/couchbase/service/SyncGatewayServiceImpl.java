package org.vsanyc.sandbox.couchbase.service;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.vsanyc.sandbox.couchbase.entities.BulkOptions;
import org.vsanyc.sandbox.couchbase.entities.ChangesOptions;
import org.vsanyc.sandbox.couchbase.entities.SgDocBody;
import org.vsanyc.sandbox.couchbase.entities.UserOptions;

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SyncGatewayServiceImpl implements SyncGatewayService {

    private Logger logger = LoggerFactory.getLogger(SyncGatewayServiceImpl.class);

    private static final String BULK_PATH = "/{0}/_bulk_docs";
    private static final String ALL_DOCS_PATH = "/{0}/_all_docs?access=false&channels=true&include_docs=false&revs=false"
            + "&update_seq=false";

    private static final String SYNC_GATEWAY_ADMIN_USER_PATH = "/{0}/_user/";
    private static final String SYNC_GATEWAY_CHANGES_PATH = "/{0}/_changes/";

    private static final String TOTAL_ROWS = "total_rows";
    private static final String TOTAL_DOCUMENTS = "total_docs";
    private static final String ROWS = "rows";


    @Value("${sync_gateway.url.template}")
    private String syncGatewayTemplatePath;

    @Value("${sync_gateway.admin.url}")
    private String syncGatewayAdminUrl;

    @Override
    public ResponseEntity createDocuments(BulkOptions bulkOptions) {
        String path = MessageFormat.format(syncGatewayTemplatePath, bulkOptions.getUsername(),
                        bulkOptions.getPassword())
                + MessageFormat.format(BULK_PATH, bulkOptions.getBucket());
        SgDocBody body = createDocs(bulkOptions.getUsername(), bulkOptions.getChannels(),
                bulkOptions.getCount());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createSyncGatewayHeaders(bulkOptions.getUsername(), bulkOptions.getPassword());
        HttpEntity<SgDocBody> bodyHttpEntity = new HttpEntity<>(body, headers);
        ResponseEntity responseEntity = restTemplate.postForEntity(path, bodyHttpEntity, String.class);
        HttpStatus status = responseEntity.getStatusCode();
        return responseEntity;
    }

    @Override
    public ResponseEntity getAllDocs(String userName, String password, String bucket) {
        String path = MessageFormat.format(syncGatewayTemplatePath, userName,
                        password)
                + MessageFormat.format(ALL_DOCS_PATH, bucket);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createSyncGatewayHeaders(userName, password);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity response = restTemplate.exchange(path, HttpMethod.GET, entity, String.class);
        return response;
    }

    @Override
    public Map<String, Object> getAllDocsStats(String userName, String password, String bucket) {
        String path = MessageFormat.format(syncGatewayTemplatePath, userName,
                password)
                + MessageFormat.format(ALL_DOCS_PATH, bucket);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createSyncGatewayHeaders(userName, password);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity response = restTemplate.exchange(path, HttpMethod.GET, entity, Map.class);
        Map<String, Object> map = (Map<String, Object>) response.getBody();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(TOTAL_DOCUMENTS, map.get(TOTAL_ROWS));
        List<Map<String, Object>> rows = (List<Map<String, Object>>)map.get(ROWS);
        Map<String, List<String>> docsByChannels = getDocumentsChannels(rows);
        Map<String, Integer> docsByChannelsCount = docsByChannels.entrySet().stream()
                .collect(toMap(k -> k.getKey(), k -> k.getValue().size() ));

        resultMap.put("docsByChannels",docsByChannelsCount);
        return resultMap;
    }

    @Override
    public HttpStatus addUser(UserOptions userOptions) {
        String path = syncGatewayAdminUrl
                + MessageFormat.format(SYNC_GATEWAY_ADMIN_USER_PATH, userOptions.getBucket());
        HttpHeaders headers = createSyncGatewayAdminHeaders();
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> map = new HashMap<>();
        map.put("name", userOptions.getName());
        map.put("password", userOptions.getPassword());
        map.put("admin_channels", userOptions.getAdminChannels());
        map.put("admin_roles", new ArrayList<>());
        map.put("email",userOptions.getName() + "@email.com");
        map.put("disabled", false);
        HttpEntity<Map> bodyHttpEntity = new HttpEntity<>(map, headers);
        ResponseEntity response = restTemplate.postForEntity(path, bodyHttpEntity, Map.class);
        return response.getStatusCode();
    }

    @Override
    public ResponseEntity getSyncGatewayUsers(String bucket) {
        String path = syncGatewayAdminUrl
                + MessageFormat.format(SYNC_GATEWAY_ADMIN_USER_PATH, bucket);
        HttpHeaders headers = createSyncGatewayAdminHeaders();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Object> bodyHttpEntity = new HttpEntity<>(headers);
        ResponseEntity response = restTemplate.exchange(path, HttpMethod.GET, bodyHttpEntity, String.class);
        return response;
    }

    @Override
    public ResponseEntity getChanges(String bucket, ChangesOptions changesOptions) {
        String path = syncGatewayAdminUrl
                + MessageFormat.format(SYNC_GATEWAY_CHANGES_PATH, bucket);
        String parameters = createGetChangesParameters(changesOptions);

        String url = path + parameters;
        HttpHeaders headers = createSyncGatewayHeaders(changesOptions.getUserName(), changesOptions.getPassword());
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Object> bodyHttpEntity = new HttpEntity<>(headers);
        ResponseEntity response = restTemplate.exchange(url, HttpMethod.GET, bodyHttpEntity, String.class);
        return response;
    }

    private String createGetChangesParameters(ChangesOptions changesOptions) {
        StringBuilder parameters = new StringBuilder("?");
        if (changesOptions.getFilter() != null) {
            parameters.append("filter=").append(changesOptions.getFilter());
        }
        if (changesOptions.getChannels() != null) {
            parameters.append("&").append("channels=").append(changesOptions.getChannels());
        }

        return parameters.toString();
    }

    private Map<String, List<String>> getDocumentsChannels(final List<Map<String, Object>> rows) {
        Map<String, List<String>> docChannels = new HashMap<>();
        for(Map<String, Object> row : rows) {
            String id = (String)row.get("id");
            Map<String, Object> value = (Map<String, Object>) row.get("value");
            List<String> channels = (List<String>) value.get("channels");
            if (channels.size() > 1) {
                logger.info("document [{}] belongs more than 1 channel", id);
            }
            for (String channel : channels) {
                addChannelId(docChannels, channel, id);
            }
        }

        return docChannels;
    }

    private void addChannelId(Map<String, List<String>> map, String channel, String id) {
        if (map.containsKey(channel)) {
            List<String> ids = map.get(channel);
            if (ids != null) {
                ids.add(id);
            } else {
                addId(map, channel, id);
            }
        } else {
            addId(map, channel, id);
        }
    }

    private void addId(Map<String, List<String>> map, String channel, String id) {
        List<String> ids = new ArrayList<>();
        ids.add(id);
        map.put(channel, ids);
    }

    private SgDocBody createDocs(String user, List<String> channels, int count) {
        SgDocBody body = new SgDocBody();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date date = new Date();
        String chs = channels.stream().collect(joining("$"));
        String valuePrefix = user + "_" + chs + "_";
        for (int i = 0; i < count; i++) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("channels", channels != null ? channels : new ArrayList<>());
            doc.put("username", user);
            String value = valuePrefix + i + "_" + sdf.format(date);
            doc.put("value", value);
            body.getDocs().add(doc);
        }
        return body;
    }

    private HttpHeaders createSyncGatewayHeaders(String username, String password) {
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

    private HttpHeaders createSyncGatewayAdminHeaders() {
        return new HttpHeaders() {{
            set("Content-Type", "application/json");
            set("accept", "application/json");
        }};
    }
}
