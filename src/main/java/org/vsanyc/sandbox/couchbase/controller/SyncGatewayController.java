package org.vsanyc.sandbox.couchbase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vsanyc.sandbox.couchbase.entities.BulkOptions;
import org.vsanyc.sandbox.couchbase.service.SyncGatewayService;

@RestController
@RequestMapping("sg")
public class SyncGatewayController {

    @Autowired
    private SyncGatewayService syncGatewayService;

    @PostMapping("/create")
    public HttpStatus createDocs(@RequestBody BulkOptions bulkOptions) {
        return syncGatewayService.createDocuments(bulkOptions);
    }
}
