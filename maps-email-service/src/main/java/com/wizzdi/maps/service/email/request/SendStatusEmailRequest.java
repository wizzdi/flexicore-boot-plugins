package com.wizzdi.maps.service.email.request;

import com.wizzdi.maps.service.email.context.StatusEmailHeaders;
import com.wizzdi.maps.service.request.StatusHistoryForDateRequest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.csv.CSVFormat;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SendStatusEmailRequest {

    @NotNull
    private StatusHistoryForDateRequest statusHistoryForDateRequest;
    @NotNull
    @NotEmpty
    private Set<String> emails;

    private ZoneOffset zoneOffset;

    private String title;

    private boolean direct;
    private CSVFormat csvFormat;

    private Map<StatusEmailHeaders,String> headerNames=new HashMap<>();


    public StatusHistoryForDateRequest getStatusHistoryForDateRequest() {
        return statusHistoryForDateRequest;
    }

    public <T extends SendStatusEmailRequest> T setStatusHistoryForDateRequest(StatusHistoryForDateRequest statusHistoryForDateRequest) {
        this.statusHistoryForDateRequest = statusHistoryForDateRequest;
        return (T) this;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public <T extends SendStatusEmailRequest> T setEmails(Set<String> emails) {
        this.emails = emails;
        return (T) this;
    }

    public ZoneOffset getZoneOffset() {
        return zoneOffset;
    }

    public <T extends SendStatusEmailRequest> T setZoneOffset(ZoneOffset zoneOffset) {
        this.zoneOffset = zoneOffset;
        return (T) this;
    }

    public String getTitle() {
        return title;
    }

    public <T extends SendStatusEmailRequest> T setTitle(String title) {
        this.title = title;
        return (T) this;
    }

    public boolean isDirect() {
        return direct;
    }

    public <T extends SendStatusEmailRequest> T setDirect(boolean direct) {
        this.direct = direct;
        return (T) this;
    }

    public CSVFormat getCsvFormat() {
        return csvFormat;
    }

    public <T extends SendStatusEmailRequest> T setCsvFormat(CSVFormat csvFormat) {
        this.csvFormat = csvFormat;
        return (T) this;
    }

    public Map<StatusEmailHeaders, String> getHeaderNames() {
        return headerNames;
    }

    public <T extends SendStatusEmailRequest> T setHeaderNames(Map<StatusEmailHeaders, String> headerNames) {
        this.headerNames = headerNames;
        return (T) this;
    }
}
