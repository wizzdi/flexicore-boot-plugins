package com.flexicore.organization.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Organization;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class EmployeeCreate extends BasicCreate {

    @JsonIgnore
    private Organization organization;
    private String organizationId;
    private String externalId;
    @JsonIgnore
    public Organization getOrganization() {
        return organization;
    }

    public <T extends EmployeeCreate> T setOrganization(Organization organization) {
        this.organization = organization;
        return (T) this;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public <T extends EmployeeCreate> T setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
        return (T) this;
    }

    public String getExternalId() {
        return externalId;
    }

    public EmployeeCreate setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }
}
