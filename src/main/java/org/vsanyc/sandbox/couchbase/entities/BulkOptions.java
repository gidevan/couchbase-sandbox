package org.vsanyc.sandbox.couchbase.entities;

import io.swagger.annotations.ApiParam;

public class BulkOptions {
    private String username;
    private String password;
    private int count;
    @ApiParam(defaultValue = "dev-sync-2")
    private String bucket;
    private String channel;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
