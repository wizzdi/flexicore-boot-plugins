package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.WhatsAppCloudHealthNotificationConfiguration;

public class WhatsAppCloudHealthNotificationConfigurationUpdate extends WhatsAppCloudHealthNotificationConfigurationCreate {
    private String id;
    @JsonIgnore
    private WhatsAppCloudHealthNotificationConfiguration configuration;

    public String getId() { return id; }
    public <T extends WhatsAppCloudHealthNotificationConfigurationUpdate> T setId(String id) { this.id = id; return (T) this; }
    public WhatsAppCloudHealthNotificationConfiguration getConfiguration() { return configuration; }
    public <T extends WhatsAppCloudHealthNotificationConfigurationUpdate> T setConfiguration(WhatsAppCloudHealthNotificationConfiguration configuration) { this.configuration = configuration; return (T) this; }
}
