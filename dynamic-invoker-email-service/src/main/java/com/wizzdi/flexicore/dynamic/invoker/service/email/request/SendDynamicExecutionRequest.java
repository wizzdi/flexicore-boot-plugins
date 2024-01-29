package com.wizzdi.flexicore.dynamic.invoker.service.email.request;


import com.wizzdi.flexicore.dynamic.invoker.service.export.request.ExportDynamicExecution;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.ZoneOffset;
import java.util.Set;

public class SendDynamicExecutionRequest {

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

    public <T extends SendDynamicExecutionRequest> T setExportDynamicExecution(ExportDynamicExecution exportDynamicExecution) {
        this.exportDynamicExecution = exportDynamicExecution;
        return (T) this;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public <T extends SendDynamicExecutionRequest> T setEmails(Set<String> emails) {
        this.emails = emails;
        return (T) this;
    }

    public ZoneOffset getZoneOffset() {
        return zoneOffset;
    }

    public <T extends SendDynamicExecutionRequest> T setZoneOffset(ZoneOffset zoneOffset) {
        this.zoneOffset = zoneOffset;
        return (T) this;
    }

    public String getTitle() {
        return title;
    }

    public <T extends SendDynamicExecutionRequest> T setTitle(String title) {
        this.title = title;
        return (T) this;
    }
}
