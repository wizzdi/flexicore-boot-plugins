package com.wizzdi.basic.iot.client;

import java.util.List;

public class RegisterGateway extends IOTMessage{

    private String publicKey;
    private Boolean noSignatureCapabilities;
    private Double lat;
    private Double lon;
    private List<WifiAccessPointInfo> wifiAccessPoints;

    public String getPublicKey() {
        return publicKey;
    }

    public <T extends RegisterGateway> T setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }

    public Boolean getNoSignatureCapabilities() {
        return noSignatureCapabilities;
    }

    public <T extends RegisterGateway> T setNoSignatureCapabilities(Boolean noSignatureCapabilities) {
        this.noSignatureCapabilities = noSignatureCapabilities;
        return (T) this;
    }

    public Double getLat() {
        return lat;
    }

    public <T extends RegisterGateway> T setLat(Double lat) {
        this.lat = lat;
        return (T) this;
    }

    public Double getLon() {
        return lon;
    }

    public <T extends RegisterGateway> T setLon(Double lon) {
        this.lon = lon;
        return (T) this;
    }

    public List<WifiAccessPointInfo> getWifiAccessPoints() {
        return wifiAccessPoints;
    }

    public <T extends RegisterGateway> T setWifiAccessPoints(List<WifiAccessPointInfo> wifiAccessPoints) {
        this.wifiAccessPoints = wifiAccessPoints;
        return (T) this;
    }

    @Override
    public boolean isRequireAuthentication() {
        return false;
    }


}
