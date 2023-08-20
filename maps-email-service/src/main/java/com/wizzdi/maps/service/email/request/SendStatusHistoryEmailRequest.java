package com.wizzdi.maps.service.email.request;

import com.wizzdi.maps.service.email.context.StatusEmailHeaders;
import com.wizzdi.maps.service.request.StatusHistoryFilter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.csv.CSVFormat;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SendStatusHistoryEmailRequest {

    @NotNull
    private StatusHistoryFilter statusHistoryFilter;
    @NotNull
    @NotEmpty
    private Set<String> emails;

    private ZoneOffset zoneOffset;

    private String title;

    private boolean direct;
    private CSVFormat csvFormat;

    private Map<StatusEmailHeaders,String> headerNames=new HashMap<>();


    public StatusHistoryFilter getStatusHistoryFilter() {
        return statusHistoryFilter;
    }

    public <T extends SendStatusHistoryEmailRequest> T setStatusHistoryFilter(StatusHistoryFilter statusHistoryFilter) {
        this.statusHistoryFilter = statusHistoryFilter;
        return (T) this;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public <T extends SendStatusHistoryEmailRequest> T setEmails(Set<String> emails) {
        this.emails = emails;
        return (T) this;
    }

    public ZoneOffset getZoneOffset() {
        return zoneOffset;
    }

    public <T extends SendStatusHistoryEmailRequest> T setZoneOffset(ZoneOffset zoneOffset) {
        this.zoneOffset = zoneOffset;
        return (T) this;
    }

    public String getTitle() {
        return title;
    }

    public <T extends SendStatusHistoryEmailRequest> T setTitle(String title) {
        this.title = title;
        return (T) this;
    }

    public boolean isDirect() {
        return direct;
    }

    public <T extends SendStatusHistoryEmailRequest> T setDirect(boolean direct) {
        this.direct = direct;
        return (T) this;
    }

    public CSVFormat getCsvFormat() {
        return csvFormat;
    }

    public <T extends SendStatusHistoryEmailRequest> T setCsvFormat(CSVFormat csvFormat) {
        this.csvFormat = csvFormat;
        return (T) this;
    }

    public Map<StatusEmailHeaders, String> getHeaderNames() {
        return headerNames;
    }

    public <T extends SendStatusHistoryEmailRequest> T setHeaderNames(Map<StatusEmailHeaders, String> headerNames) {
        this.headerNames = headerNames;
        return (T) this;
    }
}
