package org.vsanyc.sandbox.couchbase.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vsanyc.sandbox.couchbase.entities.BulkOptions;
import org.vsanyc.sandbox.couchbase.entities.ChangesOptions;
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
    public ResponseEntity createDocs(@RequestBody BulkOptions bulkOptions) {
        return syncGatewayService.createDocuments(bulkOptions);
    }

    @ApiOperation("Create user.")
    @PostMapping("/user/create")
    public HttpStatus createUser(@RequestBody UserOptions userOptions) {
        return syncGatewayService.addUser(userOptions);
    }

    @ApiOperation("call sync_gateway /_all_docs for user")
    @GetMapping("/docs/bucket/{bucket}/user/{userName}/password/{password}")
    public ResponseEntity getAllDocs(@PathVariable("userName") String userName,
                                     @PathVariable("password") String password,
                                     @PathVariable("bucket") @ApiParam(defaultValue = "dev-sync-2") String bucket) {
        return syncGatewayService.getAllDocs(userName, password, bucket);
    }

    @ApiOperation("call sync_gateway /_all_docs for user. get channel statistics")
    @GetMapping("/docs/stats/bucket/{bucket}/user/{userName}/password/{password}")
    public Map<String, Object> getAllDocsStats(
                        @PathVariable("userName") String userName,
                        @PathVariable("password") String password,
                        @PathVariable("bucket") @ApiParam(defaultValue = "dev-sync-2") String bucket) {
        return syncGatewayService.getAllDocsStats(userName, password, bucket);
    }

    @ApiOperation("Get sync_gateway users")
    @GetMapping("/bucket/{bucket}/users")
    public ResponseEntity getUsers(@PathVariable("bucket") @ApiParam(defaultValue = "dev-sync-2") String bucket) {
        return syncGatewayService.getSyncGatewayUsers(bucket);
    }

    @ApiOperation("Get sync_gateway changes")
    @PostMapping("/bucket/{bucket}/changes")
    public ResponseEntity getChanges(@PathVariable("bucket") @ApiParam(defaultValue = "dev-sync-2") String bucket,
                                     @RequestBody  ChangesOptions changesOptions) {
        return syncGatewayService.getChanges(bucket, changesOptions);
    }


}
