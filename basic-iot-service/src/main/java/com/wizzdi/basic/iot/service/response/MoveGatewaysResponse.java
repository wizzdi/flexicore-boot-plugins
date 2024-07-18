package com.wizzdi.basic.iot.service.response;

public record MoveGatewaysResponse(int movedGateways, int movedRemotes,int movedMappedPOIs,int createdDeviceTypes,int createdMapIcons) {
    public static MoveGatewaysResponse empty() {
        return new MoveGatewaysResponse(0,0,0,0,0);
    }
}
