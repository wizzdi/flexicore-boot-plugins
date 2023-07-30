package com.flexicore.license.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.flexicore.license.model.LicenseRequest;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LicenseRequestToEntityFiltering extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> licenseRequestIds=new HashSet<>();
    @JsonIgnore
    private List<LicenseRequest> licenseRequests;
    private OffsetDateTime expirationDateAfter;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends LicenseRequestToEntityFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getLicenseRequestIds() {
        return licenseRequestIds;
    }

    public <T extends LicenseRequestToEntityFiltering> T setLicenseRequestIds(Set<String> licenseRequestIds) {
        this.licenseRequestIds = licenseRequestIds;
        return (T) this;
    }

    @JsonIgnore
    public List<LicenseRequest> getLicenseRequests() {
        return licenseRequests;
    }

    public <T extends LicenseRequestToEntityFiltering> T setLicenseRequests(List<LicenseRequest> licenseRequests) {
        this.licenseRequests = licenseRequests;
        return (T) this;
    }

    public OffsetDateTime getExpirationDateAfter() {
        return expirationDateAfter;
    }

    public <T extends LicenseRequestToEntityFiltering> T setExpirationDateAfter(OffsetDateTime expirationDateAfter) {
        this.expirationDateAfter = expirationDateAfter;
        return (T) this;
    }
}
