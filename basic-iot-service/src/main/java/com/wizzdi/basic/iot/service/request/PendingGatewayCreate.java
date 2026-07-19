package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.flexicore.security.request.BasicCreate;
import java.time.OffsetDateTime;

public class PendingGatewayCreate extends BasicCreate {

    private String externalId;
    private String gatewayId;
    private String publicKey;
    private String registeredGatewayId;
    @JsonIgnore
    private Gateway registeredGateway;

    private Boolean noSignatureCapabilities;
    private Double lat;
    private Double lon;
    private Double locationAccuracyMeters;
    private String locationSource;
    private String wifiAccessPointsJson;
    private Boolean gatewayConfirmationReceived;
    private OffsetDateTime gatewayConfirmationReceivedAt;
    private OffsetDateTime lastGatewayConfirmationSentAt;
    private Integer gatewayConfirmationAttempts;

    public String getExternalId() {
        return externalId;
    }

    public <T extends PendingGatewayCreate> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public <T extends PendingGatewayCreate> T setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
        return (T) this;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public <T extends PendingGatewayCreate> T setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }

    public String getRegisteredGatewayId() {
        return registeredGatewayId;
    }

    public <T extends PendingGatewayCreate> T setRegisteredGatewayId(String registeredGatewayId) {
        this.registeredGatewayId = registeredGatewayId;
        return (T) this;
    }

    @JsonIgnore
    public Gateway getRegisteredGateway() {
        return registeredGateway;
    }

    public <T extends PendingGatewayCreate> T setRegisteredGateway(Gateway registeredGateway) {
        this.registeredGateway = registeredGateway;
        return (T) this;
    }

    public Boolean getNoSignatureCapabilities() {
        return noSignatureCapabilities;
    }

    public <T extends PendingGatewayCreate> T setNoSignatureCapabilities(Boolean noSignatureCapabilities) {
        this.noSignatureCapabilities = noSignatureCapabilities;
        return (T) this;
    }

    public Double getLat() {
        return lat;
    }

    public <T extends PendingGatewayCreate> T setLat(Double lat) {
        this.lat = lat;
        return (T) this;
    }

    public Double getLon() {
        return lon;
    }

    public <T extends PendingGatewayCreate> T setLon(Double lon) {
        this.lon = lon;
        return (T) this;
    }

    public Double getLocationAccuracyMeters() {
        return locationAccuracyMeters;
    }

    public <T extends PendingGatewayCreate> T setLocationAccuracyMeters(Double locationAccuracyMeters) {
        this.locationAccuracyMeters = locationAccuracyMeters;
        return (T) this;
    }

    public String getLocationSource() {
        return locationSource;
    }

    public <T extends PendingGatewayCreate> T setLocationSource(String locationSource) {
        this.locationSource = locationSource;
        return (T) this;
    }

    public String getWifiAccessPointsJson() {
        return wifiAccessPointsJson;
    }

    public <T extends PendingGatewayCreate> T setWifiAccessPointsJson(String wifiAccessPointsJson) {
        this.wifiAccessPointsJson = wifiAccessPointsJson;
        return (T) this;
    }

    public Boolean getGatewayConfirmationReceived() {
        return gatewayConfirmationReceived;
    }

    public <T extends PendingGatewayCreate> T setGatewayConfirmationReceived(Boolean gatewayConfirmationReceived) {
        this.gatewayConfirmationReceived = gatewayConfirmationReceived;
        return (T) this;
    }

    public OffsetDateTime getGatewayConfirmationReceivedAt() {
        return gatewayConfirmationReceivedAt;
    }

    public <T extends PendingGatewayCreate> T setGatewayConfirmationReceivedAt(OffsetDateTime gatewayConfirmationReceivedAt) {
        this.gatewayConfirmationReceivedAt = gatewayConfirmationReceivedAt;
        return (T) this;
    }

    public OffsetDateTime getLastGatewayConfirmationSentAt() {
        return lastGatewayConfirmationSentAt;
    }

    public <T extends PendingGatewayCreate> T setLastGatewayConfirmationSentAt(OffsetDateTime lastGatewayConfirmationSentAt) {
        this.lastGatewayConfirmationSentAt = lastGatewayConfirmationSentAt;
        return (T) this;
    }

    public Integer getGatewayConfirmationAttempts() {
        return gatewayConfirmationAttempts;
    }

    public <T extends PendingGatewayCreate> T setGatewayConfirmationAttempts(Integer gatewayConfirmationAttempts) {
        this.gatewayConfirmationAttempts = gatewayConfirmationAttempts;
        return (T) this;
    }
}
