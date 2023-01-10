package com.wizzdi.maps.service.email.request;

import com.wizzdi.maps.service.request.MappedPOIFilter;
import com.wizzdi.maps.service.request.StatusHistoryForDateRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZoneOffset;
import java.util.Set;

public class SendStatusEmailRequest {

    @NotNull
    private StatusHistoryForDateRequest statusHistoryForDateRequest;
    @NotNull
    @NotEmpty
    private Set<String> emails;

    private ZoneOffset zoneOffset;

    private String title;

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
}
