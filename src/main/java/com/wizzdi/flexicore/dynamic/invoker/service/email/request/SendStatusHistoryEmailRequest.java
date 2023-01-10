package com.wizzdi.flexicore.dynamic.invoker.service.email.request;

import com.wizzdi.maps.service.request.StatusHistoryFilter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZoneOffset;
import java.util.Set;

public class SendStatusHistoryEmailRequest {

    @NotNull
    private StatusHistoryFilter statusHistoryFilter;
    @NotNull
    @NotEmpty
    private Set<String> emails;

    private ZoneOffset zoneOffset;

    private String title;


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
}
