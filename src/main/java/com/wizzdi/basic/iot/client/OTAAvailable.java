package com.wizzdi.basic.iot.client;

import java.time.OffsetDateTime;

public class OTAAvailable extends IOTMessage{


    private String md5;
    private String crc;
    private String version;
    private String firmwareInstallationId;
    private String baseUrl;

    private OffsetDateTime targetInstallationDate;


    public String getMd5() {
        return md5;
    }

    public <T extends OTAAvailable> T setMd5(String md5) {
        this.md5 = md5;
        return (T) this;
    }

    public String getCrc() {
        return crc;
    }

    public <T extends OTAAvailable> T setCrc(String crc) {
        this.crc = crc;
        return (T) this;
    }

    public String getVersion() {
        return version;
    }

    public <T extends OTAAvailable> T setVersion(String version) {
        this.version = version;
        return (T) this;
    }

    public String getFirmwareInstallationId() {
        return firmwareInstallationId;
    }

    public <T extends OTAAvailable> T setFirmwareInstallationId(String firmwareInstallationId) {
        this.firmwareInstallationId = firmwareInstallationId;
        return (T) this;
    }

    public OffsetDateTime getTargetInstallationDate() {
        return targetInstallationDate;
    }

    public <T extends OTAAvailable> T setTargetInstallationDate(OffsetDateTime targetInstallationDate) {
        this.targetInstallationDate = targetInstallationDate;
        return (T) this;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public <T extends OTAAvailable> T setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return (T) this;
    }

    @Override
    public boolean isRetained() {
        return false;
    }
}
