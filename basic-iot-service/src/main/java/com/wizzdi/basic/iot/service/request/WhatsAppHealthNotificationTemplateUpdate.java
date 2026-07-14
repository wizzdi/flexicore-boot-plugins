package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.WhatsAppHealthNotificationTemplate;

public class WhatsAppHealthNotificationTemplateUpdate extends WhatsAppHealthNotificationTemplateCreate {
    private String id;
    @JsonIgnore
    private WhatsAppHealthNotificationTemplate template;

    public String getId() { return id; }
    public <T extends WhatsAppHealthNotificationTemplateUpdate> T setId(String id) { this.id = id; return (T) this; }
    public WhatsAppHealthNotificationTemplate getTemplate() { return template; }
    public <T extends WhatsAppHealthNotificationTemplateUpdate> T setTemplate(WhatsAppHealthNotificationTemplate template) { this.template = template; return (T) this; }
}
