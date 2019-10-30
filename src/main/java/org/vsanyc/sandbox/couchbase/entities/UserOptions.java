package org.vsanyc.sandbox.couchbase.entities;

import io.swagger.annotations.ApiParam;

import java.util.List;

public class UserOptions {

    private String name;
    private String password;
    private List<String> adminChannels;
    private String bucket;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getAdminChannels() {
        return adminChannels;
    }

    public void setAdminChannels(List<String> adminChannels) {
        this.adminChannels = adminChannels;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
