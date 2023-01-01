package com.wizzdi.maps.service.email.request;

import com.wizzdi.maps.service.request.MappedPOIFilter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class SendStatusEmailRequest {

    private MappedPOIFilter mappedPOIFilter;
    @NotNull
    @NotEmpty
    private Set<String> emails;

    public MappedPOIFilter getMappedPOIFilter() {
        return mappedPOIFilter;
    }

    public <T extends SendStatusEmailRequest> T setMappedPOIFilter(MappedPOIFilter mappedPOIFilter) {
        this.mappedPOIFilter = mappedPOIFilter;
        return (T) this;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public <T extends SendStatusEmailRequest> T setEmails(Set<String> emails) {
        this.emails = emails;
        return (T) this;
    }
}
