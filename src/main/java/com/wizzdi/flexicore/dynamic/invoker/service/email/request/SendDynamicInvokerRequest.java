package com.wizzdi.flexicore.dynamic.invoker.service.email.request;

import com.flexicore.request.ExportDynamicExecution;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZoneOffset;
import java.util.Set;

public class SendDynamicInvokerRequest {

    @NotNull
    private ExportDynamicExecution exportDynamicExecution;
    @NotNull
    @NotEmpty
    private Set<String> emails;

    private ZoneOffset zoneOffset;

    private String title;

    public ExportDynamicExecution getExportDynamicExecution() {
        return exportDynamicExecution;
    }

    public <T extends SendDynamicInvokerRequest> T setExportDynamicExecution(ExportDynamicExecution exportDynamicExecution) {
        this.exportDynamicExecution = exportDynamicExecution;
        return (T) this;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public <T extends SendDynamicInvokerRequest> T setEmails(Set<String> emails) {
        this.emails = emails;
        return (T) this;
    }

    public ZoneOffset getZoneOffset() {
        return zoneOffset;
    }

    public <T extends SendDynamicInvokerRequest> T setZoneOffset(ZoneOffset zoneOffset) {
        this.zoneOffset = zoneOffset;
        return (T) this;
    }

    public String getTitle() {
        return title;
    }

    public <T extends SendDynamicInvokerRequest> T setTitle(String title) {
        this.title = title;
        return (T) this;
    }
}
