package org.vsanyc.sandbox.couchbase.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vsanyc.sandbox.couchbase.entities.BulkOptions;
import org.vsanyc.sandbox.couchbase.entities.UserOptions;
import org.vsanyc.sandbox.couchbase.service.SyncGatewayService;

import java.util.Map;

@RestController
@RequestMapping("sg")
public class SyncGatewayController {

    @Autowired
    private SyncGatewayService syncGatewayService;

    @ApiOperation("Create documents for user. Bulk operation")
    @PostMapping("/docs/create")
    public HttpStatus createDocs(@RequestBody BulkOptions bulkOptions) {
        return syncGatewayService.createDocuments(bulkOptions);
    }

    @ApiOperation("Create user.")
    @PostMapping("/user/create")
    public HttpStatus createUser(@RequestBody UserOptions userOptions) {
        return syncGatewayService.addUser(userOptions);
    }

    @ApiOperation("call sync_gateway /_all_docs for user")
    @GetMapping("/docs/bucket/{bucket}/user/{userName}/password/{password}")
    public ResponseEntity getAllDocs(String userName, String password, String bucket) {
        return syncGatewayService.getAllDocs(userName, password, bucket);
    }

    @ApiOperation("call sync_gateway /_all_docs for user. get channel statistics")
    @GetMapping("/docs/stats/bucket/{bucket}/user/{userName}/password/{password}")
    public Map<String, Object> getAllDocsStats(String userName, String password, String bucket) {
        return syncGatewayService.getAllDocsStats(userName, password, bucket);
    }
}
