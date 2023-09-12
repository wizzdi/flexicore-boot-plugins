package com.wizzdi.basic.iot.service.response;

public class FixRemotesResponse {

    private final int fixedRemotes;
    private final int fixedMapPOIs;
    private final int fixedRemoteDeviceTypes;
    private final int fixedDeviceTypes;

    public FixRemotesResponse(int fixedRemotes, int fixedMapPOIs,int fixedRemoteDeviceTypes,int fixedDeviceTypes) {
        this.fixedRemotes = fixedRemotes;
        this.fixedMapPOIs = fixedMapPOIs;
        this.fixedRemoteDeviceTypes = fixedRemoteDeviceTypes;
        this.fixedDeviceTypes = fixedDeviceTypes;
    }


    public int getFixedRemotes() {
        return fixedRemotes;
    }

    public int getFixedMapPOIs() {
        return fixedMapPOIs;
    }

    public int getFixedRemoteDeviceTypes() {
        return fixedRemoteDeviceTypes;
    }

    public int getFixedDeviceTypes() {
        return fixedDeviceTypes;
    }
}
