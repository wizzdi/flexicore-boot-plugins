package com.wizzdi.basic.iot.client;


public class OTAAvailableReceived extends IOTMessage{


    private String OTAAvailableId;


    public String getOTAAvailableId() {
        return OTAAvailableId;
    }

    public <T extends OTAAvailableReceived> T setOTAAvailableId(String OTAAvailableId) {
        this.OTAAvailableId = OTAAvailableId;
        return (T) this;
    }
}
