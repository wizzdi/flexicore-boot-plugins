package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BaseclassCreate;

public class WhatsAppCloudHealthNotificationConfigurationCreate extends BaseclassCreate {
    private Boolean enabled;
    private String apiBaseUrl;
    private String graphApiVersion;
    private String phoneNumberId;
    private String whatsAppBusinessAccountId;
    private String accessToken;
    private Boolean clearAccessToken;
    private String defaultTemplateName;
    private String defaultLanguageCode;

    public Boolean getEnabled() { return enabled; }
    public <T extends WhatsAppCloudHealthNotificationConfigurationCreate> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
    public String getApiBaseUrl() { return apiBaseUrl; }
    public <T extends WhatsAppCloudHealthNotificationConfigurationCreate> T setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; return (T) this; }
    public String getGraphApiVersion() { return graphApiVersion; }
    public <T extends WhatsAppCloudHealthNotificationConfigurationCreate> T setGraphApiVersion(String graphApiVersion) { this.graphApiVersion = graphApiVersion; return (T) this; }
    public String getPhoneNumberId() { return phoneNumberId; }
    public <T extends WhatsAppCloudHealthNotificationConfigurationCreate> T setPhoneNumberId(String phoneNumberId) { this.phoneNumberId = phoneNumberId; return (T) this; }
    public String getWhatsAppBusinessAccountId() { return whatsAppBusinessAccountId; }
    public <T extends WhatsAppCloudHealthNotificationConfigurationCreate> T setWhatsAppBusinessAccountId(String whatsAppBusinessAccountId) { this.whatsAppBusinessAccountId = whatsAppBusinessAccountId; return (T) this; }
    public String getAccessToken() { return accessToken; }
    public <T extends WhatsAppCloudHealthNotificationConfigurationCreate> T setAccessToken(String accessToken) { this.accessToken = accessToken; return (T) this; }
    public Boolean getClearAccessToken() { return clearAccessToken; }
    public <T extends WhatsAppCloudHealthNotificationConfigurationCreate> T setClearAccessToken(Boolean clearAccessToken) { this.clearAccessToken = clearAccessToken; return (T) this; }
    public String getDefaultTemplateName() { return defaultTemplateName; }
    public <T extends WhatsAppCloudHealthNotificationConfigurationCreate> T setDefaultTemplateName(String defaultTemplateName) { this.defaultTemplateName = defaultTemplateName; return (T) this; }
    public String getDefaultLanguageCode() { return defaultLanguageCode; }
    public <T extends WhatsAppCloudHealthNotificationConfigurationCreate> T setDefaultLanguageCode(String defaultLanguageCode) { this.defaultLanguageCode = defaultLanguageCode; return (T) this; }
}
