package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.SendGridHealthNotificationConfiguration;

public class SendGridHealthNotificationConfigurationUpdate extends SendGridHealthNotificationConfigurationCreate {
    private String id;
    @JsonIgnore
    private SendGridHealthNotificationConfiguration configuration;

    public String getId() { return id; }
    public <T extends SendGridHealthNotificationConfigurationUpdate> T setId(String id) { this.id = id; return (T) this; }
    public SendGridHealthNotificationConfiguration getConfiguration() { return configuration; }
    public <T extends SendGridHealthNotificationConfigurationUpdate> T setConfiguration(SendGridHealthNotificationConfiguration configuration) { this.configuration = configuration; return (T) this; }
}
