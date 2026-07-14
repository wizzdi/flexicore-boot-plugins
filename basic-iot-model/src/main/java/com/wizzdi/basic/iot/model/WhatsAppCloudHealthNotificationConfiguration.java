package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.OffsetDateTime;

@Entity
@Table(indexes = {
        @Index(name = "whatsapp_health_config_tenant_idx", columnList = "tenant_id,enabled,softdelete")
})
public class WhatsAppCloudHealthNotificationConfiguration extends Baseclass {
    private boolean enabled = true;
    @Column(nullable = false)
    private String apiBaseUrl = "https://graph.facebook.com";
    @Column(nullable = false)
    private String graphApiVersion = "v25.0";
    private String phoneNumberId;
    private String whatsAppBusinessAccountId;
    @JsonIgnore
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "bytea")
    private byte[] encryptedAccessToken;
    private String accessTokenFingerprint;
    private String defaultTemplateName;
    private String defaultLanguageCode = "en_US";
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime credentialUpdatedAt;

    public boolean isEnabled() { return enabled; }
    public <T extends WhatsAppCloudHealthNotificationConfiguration> T setEnabled(boolean enabled) { this.enabled = enabled; return (T) this; }
    public String getApiBaseUrl() { return apiBaseUrl; }
    public <T extends WhatsAppCloudHealthNotificationConfiguration> T setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; return (T) this; }
    public String getGraphApiVersion() { return graphApiVersion; }
    public <T extends WhatsAppCloudHealthNotificationConfiguration> T setGraphApiVersion(String graphApiVersion) { this.graphApiVersion = graphApiVersion; return (T) this; }
    public String getPhoneNumberId() { return phoneNumberId; }
    public <T extends WhatsAppCloudHealthNotificationConfiguration> T setPhoneNumberId(String phoneNumberId) { this.phoneNumberId = phoneNumberId; return (T) this; }
    public String getWhatsAppBusinessAccountId() { return whatsAppBusinessAccountId; }
    public <T extends WhatsAppCloudHealthNotificationConfiguration> T setWhatsAppBusinessAccountId(String whatsAppBusinessAccountId) { this.whatsAppBusinessAccountId = whatsAppBusinessAccountId; return (T) this; }
    @JsonIgnore
    public byte[] getEncryptedAccessToken() { return encryptedAccessToken; }
    public <T extends WhatsAppCloudHealthNotificationConfiguration> T setEncryptedAccessToken(byte[] encryptedAccessToken) { this.encryptedAccessToken = encryptedAccessToken; return (T) this; }
    public String getAccessTokenFingerprint() { return accessTokenFingerprint; }
    public <T extends WhatsAppCloudHealthNotificationConfiguration> T setAccessTokenFingerprint(String accessTokenFingerprint) { this.accessTokenFingerprint = accessTokenFingerprint; return (T) this; }
    @Transient
    public boolean isAccessTokenConfigured() { return encryptedAccessToken != null && encryptedAccessToken.length > 0; }
    public String getDefaultTemplateName() { return defaultTemplateName; }
    public <T extends WhatsAppCloudHealthNotificationConfiguration> T setDefaultTemplateName(String defaultTemplateName) { this.defaultTemplateName = defaultTemplateName; return (T) this; }
    public String getDefaultLanguageCode() { return defaultLanguageCode; }
    public <T extends WhatsAppCloudHealthNotificationConfiguration> T setDefaultLanguageCode(String defaultLanguageCode) { this.defaultLanguageCode = defaultLanguageCode; return (T) this; }
    public OffsetDateTime getCredentialUpdatedAt() { return credentialUpdatedAt; }
    public <T extends WhatsAppCloudHealthNotificationConfiguration> T setCredentialUpdatedAt(OffsetDateTime credentialUpdatedAt) { this.credentialUpdatedAt = credentialUpdatedAt; return (T) this; }
}
