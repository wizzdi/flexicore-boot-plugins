package com.wizzdi.flexicore.dynamic.invoker.service.email.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SendEmailPlainRequest {

    @NotNull
    @NotEmpty
    private Set<String> emails;



    @NotEmpty
    private String replyTo;

    private String replyToAlias;
    @NotEmpty
    private String from;

    private String fromAlias;

    @NotEmpty
    private String templateId;

    private Map<String,Object> additionalProperties=new HashMap<>();


    public Set<String> getEmails() {
        return emails;
    }

    public <T extends SendEmailPlainRequest> T setEmails(Set<String> emails) {
        this.emails = emails;
        return (T) this;
    }


    public String getTemplateId() {
        return templateId;
    }

    public <T extends SendEmailPlainRequest> T setTemplateId(String templateId) {
        this.templateId = templateId;
        return (T) this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public <T extends SendEmailPlainRequest> T setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
        return (T) this;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public <T extends SendEmailPlainRequest> T setReplyTo(String replyTo) {
        this.replyTo = replyTo;
        return (T) this;
    }

    public String getFrom() {
        return from;
    }

    public <T extends SendEmailPlainRequest> T setFrom(String from) {
        this.from = from;
        return (T) this;
    }

    public String getReplyToAlias() {
        return replyToAlias;
    }

    public <T extends SendEmailPlainRequest> T setReplyToAlias(String replyToAlias) {
        this.replyToAlias = replyToAlias;
        return (T) this;
    }

    public String getFromAlias() {
        return fromAlias;
    }

    public <T extends SendEmailPlainRequest> T setFromAlias(String fromAlias) {
        this.fromAlias = fromAlias;
        return (T) this;
    }
}
