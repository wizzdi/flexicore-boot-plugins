package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BaseclassCreate;

public class SendGridHealthNotificationConfigurationCreate extends BaseclassCreate {
    private Boolean enabled;
    private String apiBaseUrl;
    private String apiKey;
    private Boolean clearApiKey;
    private String fromEmail;
    private String fromName;
    private String replyToEmail;
    private String replyToName;
    private String onBehalfOf;
    private String defaultTemplateId;

    public Boolean getEnabled() { return enabled; }
    public <T extends SendGridHealthNotificationConfigurationCreate> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
    public String getApiBaseUrl() { return apiBaseUrl; }
    public <T extends SendGridHealthNotificationConfigurationCreate> T setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; return (T) this; }
    public String getApiKey() { return apiKey; }
    public <T extends SendGridHealthNotificationConfigurationCreate> T setApiKey(String apiKey) { this.apiKey = apiKey; return (T) this; }
    public Boolean getClearApiKey() { return clearApiKey; }
    public <T extends SendGridHealthNotificationConfigurationCreate> T setClearApiKey(Boolean clearApiKey) { this.clearApiKey = clearApiKey; return (T) this; }
    public String getFromEmail() { return fromEmail; }
    public <T extends SendGridHealthNotificationConfigurationCreate> T setFromEmail(String fromEmail) { this.fromEmail = fromEmail; return (T) this; }
    public String getFromName() { return fromName; }
    public <T extends SendGridHealthNotificationConfigurationCreate> T setFromName(String fromName) { this.fromName = fromName; return (T) this; }
    public String getReplyToEmail() { return replyToEmail; }
    public <T extends SendGridHealthNotificationConfigurationCreate> T setReplyToEmail(String replyToEmail) { this.replyToEmail = replyToEmail; return (T) this; }
    public String getReplyToName() { return replyToName; }
    public <T extends SendGridHealthNotificationConfigurationCreate> T setReplyToName(String replyToName) { this.replyToName = replyToName; return (T) this; }
    public String getOnBehalfOf() { return onBehalfOf; }
    public <T extends SendGridHealthNotificationConfigurationCreate> T setOnBehalfOf(String onBehalfOf) { this.onBehalfOf = onBehalfOf; return (T) this; }
    public String getDefaultTemplateId() { return defaultTemplateId; }
    public <T extends SendGridHealthNotificationConfigurationCreate> T setDefaultTemplateId(String defaultTemplateId) { this.defaultTemplateId = defaultTemplateId; return (T) this; }
}
