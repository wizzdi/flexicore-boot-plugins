package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(indexes = {
        @Index(name = "pending_gateway_idx",columnList = "gatewayId,registeredGateway_id")
})
public class PendingGateway extends Baseclass {

    @Lob
    private String gatewayId;
    @Lob
    private String publicKey;
    @OneToOne(targetEntity = Gateway.class)
    private Gateway registeredGateway;
    private boolean noSignatureCapabilities;
    private Double lat;
    private Double lon;
    private Double locationAccuracyMeters;
    private String locationSource;
    @Lob
    private String wifiAccessPointsJson;
    private boolean gatewayConfirmationReceived;
    private OffsetDateTime gatewayConfirmationReceivedAt;
    private OffsetDateTime lastGatewayConfirmationSentAt;
    private Integer gatewayConfirmationAttempts;

    public String getGatewayId() {
        return gatewayId;
    }

    public <T extends PendingGateway> T setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
        return (T) this;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public <T extends PendingGateway> T setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }

    @OneToOne(targetEntity = Gateway.class)
    public Gateway getRegisteredGateway() {
        return registeredGateway;
    }

    public <T extends PendingGateway> T setRegisteredGateway(Gateway registeredGateway) {
        this.registeredGateway = registeredGateway;
        return (T) this;
    }

    public boolean isNoSignatureCapabilities() {
        return noSignatureCapabilities;
    }

    public <T extends PendingGateway> T setNoSignatureCapabilities(boolean noSignatureCapabilities) {
        this.noSignatureCapabilities = noSignatureCapabilities;
        return (T) this;
    }

    public Double getLat() {
        return lat;
    }

    public <T extends PendingGateway> T setLat(Double lat) {
        this.lat = lat;
        return (T) this;
    }

    public Double getLon() {
        return lon;
    }

    public <T extends PendingGateway> T setLon(Double lon) {
        this.lon = lon;
        return (T) this;
    }

    public Double getLocationAccuracyMeters() {
        return locationAccuracyMeters;
    }

    public <T extends PendingGateway> T setLocationAccuracyMeters(Double locationAccuracyMeters) {
        this.locationAccuracyMeters = locationAccuracyMeters;
        return (T) this;
    }

    public String getLocationSource() {
        return locationSource;
    }

    public <T extends PendingGateway> T setLocationSource(String locationSource) {
        this.locationSource = locationSource;
        return (T) this;
    }

    public String getWifiAccessPointsJson() {
        return wifiAccessPointsJson;
    }

    public <T extends PendingGateway> T setWifiAccessPointsJson(String wifiAccessPointsJson) {
        this.wifiAccessPointsJson = wifiAccessPointsJson;
        return (T) this;
    }

    public boolean isGatewayConfirmationReceived() {
        return gatewayConfirmationReceived;
    }

    public <T extends PendingGateway> T setGatewayConfirmationReceived(boolean gatewayConfirmationReceived) {
        this.gatewayConfirmationReceived = gatewayConfirmationReceived;
        return (T) this;
    }

    public OffsetDateTime getGatewayConfirmationReceivedAt() {
        return gatewayConfirmationReceivedAt;
    }

    public <T extends PendingGateway> T setGatewayConfirmationReceivedAt(OffsetDateTime gatewayConfirmationReceivedAt) {
        this.gatewayConfirmationReceivedAt = gatewayConfirmationReceivedAt;
        return (T) this;
    }

    public OffsetDateTime getLastGatewayConfirmationSentAt() {
        return lastGatewayConfirmationSentAt;
    }

    public <T extends PendingGateway> T setLastGatewayConfirmationSentAt(OffsetDateTime lastGatewayConfirmationSentAt) {
        this.lastGatewayConfirmationSentAt = lastGatewayConfirmationSentAt;
        return (T) this;
    }

    public Integer getGatewayConfirmationAttempts() {
        return gatewayConfirmationAttempts;
    }

    public <T extends PendingGateway> T setGatewayConfirmationAttempts(Integer gatewayConfirmationAttempts) {
        this.gatewayConfirmationAttempts = gatewayConfirmationAttempts;
        return (T) this;
    }
}
