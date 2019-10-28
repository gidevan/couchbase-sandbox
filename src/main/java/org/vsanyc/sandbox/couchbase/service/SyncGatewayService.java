package org.vsanyc.sandbox.couchbase.service;

import org.springframework.http.HttpStatus;
import org.vsanyc.sandbox.couchbase.entities.BulkOptions;

public interface SyncGatewayService {

    HttpStatus createDocuments(BulkOptions bulkOptions);

    void getAllDocs(String userName, String password, String bucket);

}
