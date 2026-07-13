package com.wizzdi.basic.iot.service.request;

public class EvaluateRemoteGroupHealthRequest {
    private String remoteGroupId;

    public String getRemoteGroupId() { return remoteGroupId; }
    public <T extends EvaluateRemoteGroupHealthRequest> T setRemoteGroupId(String remoteGroupId) { this.remoteGroupId = remoteGroupId; return (T) this; }
}
