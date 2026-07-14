package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.SendGridHealthNotificationTemplate;

public class SendGridHealthNotificationTemplateUpdate extends SendGridHealthNotificationTemplateCreate {
    private String id;
    @JsonIgnore
    private SendGridHealthNotificationTemplate template;

    public String getId() { return id; }
    public <T extends SendGridHealthNotificationTemplateUpdate> T setId(String id) { this.id = id; return (T) this; }
    public SendGridHealthNotificationTemplate getTemplate() { return template; }
    public <T extends SendGridHealthNotificationTemplateUpdate> T setTemplate(SendGridHealthNotificationTemplate template) { this.template = template; return (T) this; }
}
