package org.vsanyc.sandbox.couchbase.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.vsanyc.sandbox.couchbase.entities.BulkOptions;
import org.vsanyc.sandbox.couchbase.entities.ChangesOptions;
import org.vsanyc.sandbox.couchbase.entities.UserOptions;

import java.util.List;
import java.util.Map;

public interface SyncGatewayService {

    ResponseEntity createDocuments(BulkOptions bulkOptions);

    ResponseEntity getAllDocs(String userName, String password, String bucket);

    Map<String, Object> getAllDocsStats(String userName, String password, String bucket);

    HttpStatus addUser(UserOptions userOptions);

    ResponseEntity getSyncGatewayUsers(String bucket);

    ResponseEntity getChanges(String bucket, ChangesOptions changesOptions);

}
