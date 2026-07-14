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
        @Index(name = "sendgrid_health_config_tenant_idx", columnList = "tenant_id,enabled,softdelete")
})
public class SendGridHealthNotificationConfiguration extends Baseclass {
    private boolean enabled = true;
    @Column(nullable = false)
    private String apiBaseUrl = "https://api.sendgrid.com";
    @JsonIgnore
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "bytea")
    private byte[] encryptedApiKey;
    private String apiKeyFingerprint;
    private String fromEmail;
    private String fromName;
    private String replyToEmail;
    private String replyToName;
    private String onBehalfOf;
    private String defaultTemplateId;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime credentialUpdatedAt;

    public boolean isEnabled() { return enabled; }
    public <T extends SendGridHealthNotificationConfiguration> T setEnabled(boolean enabled) { this.enabled = enabled; return (T) this; }
    public String getApiBaseUrl() { return apiBaseUrl; }
    public <T extends SendGridHealthNotificationConfiguration> T setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; return (T) this; }
    @JsonIgnore
    public byte[] getEncryptedApiKey() { return encryptedApiKey; }
    public <T extends SendGridHealthNotificationConfiguration> T setEncryptedApiKey(byte[] encryptedApiKey) { this.encryptedApiKey = encryptedApiKey; return (T) this; }
    public String getApiKeyFingerprint() { return apiKeyFingerprint; }
    public <T extends SendGridHealthNotificationConfiguration> T setApiKeyFingerprint(String apiKeyFingerprint) { this.apiKeyFingerprint = apiKeyFingerprint; return (T) this; }
    @Transient
    public boolean isApiKeyConfigured() { return encryptedApiKey != null && encryptedApiKey.length > 0; }
    public String getFromEmail() { return fromEmail; }
    public <T extends SendGridHealthNotificationConfiguration> T setFromEmail(String fromEmail) { this.fromEmail = fromEmail; return (T) this; }
    public String getFromName() { return fromName; }
    public <T extends SendGridHealthNotificationConfiguration> T setFromName(String fromName) { this.fromName = fromName; return (T) this; }
    public String getReplyToEmail() { return replyToEmail; }
    public <T extends SendGridHealthNotificationConfiguration> T setReplyToEmail(String replyToEmail) { this.replyToEmail = replyToEmail; return (T) this; }
    public String getReplyToName() { return replyToName; }
    public <T extends SendGridHealthNotificationConfiguration> T setReplyToName(String replyToName) { this.replyToName = replyToName; return (T) this; }
    public String getOnBehalfOf() { return onBehalfOf; }
    public <T extends SendGridHealthNotificationConfiguration> T setOnBehalfOf(String onBehalfOf) { this.onBehalfOf = onBehalfOf; return (T) this; }
    public String getDefaultTemplateId() { return defaultTemplateId; }
    public <T extends SendGridHealthNotificationConfiguration> T setDefaultTemplateId(String defaultTemplateId) { this.defaultTemplateId = defaultTemplateId; return (T) this; }
    public OffsetDateTime getCredentialUpdatedAt() { return credentialUpdatedAt; }
    public <T extends SendGridHealthNotificationConfiguration> T setCredentialUpdatedAt(OffsetDateTime credentialUpdatedAt) { this.credentialUpdatedAt = credentialUpdatedAt; return (T) this; }
}
