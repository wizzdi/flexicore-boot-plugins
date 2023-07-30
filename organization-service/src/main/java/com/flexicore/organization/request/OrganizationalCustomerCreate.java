package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Organization;

public class OrganizationalCustomerCreate extends CustomerCreate {

    private String organizationId;
    @JsonIgnore
    private Organization organization;

    public String getOrganizationId() {
        return organizationId;
    }

    public <T extends OrganizationalCustomerCreate> T setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
        return (T) this;
    }

    @JsonIgnore
    public Organization getOrganization() {
        return organization;
    }

    public <T extends OrganizationalCustomerCreate> T setOrganization(Organization organization) {
        this.organization = organization;
        return (T) this;
    }

}
