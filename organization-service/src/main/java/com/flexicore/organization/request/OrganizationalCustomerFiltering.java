package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.flexicore.model.SecurityUser;
import com.flexicore.organization.model.Organization;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrganizationalCustomerFiltering extends CustomerFiltering {

    private Set<String> organizationIds=new HashSet<>();
    @JsonIgnore
    @TypeRetention(Organization.class)
    private List<Organization> organizations;


    public Set<String> getOrganizationIds() {
        return organizationIds;
    }

    public <T extends OrganizationalCustomerFiltering> T setOrganizationIds(Set<String> organizationIds) {
        this.organizationIds = organizationIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Organization> getOrganizations() {
        return organizations;
    }

    public <T extends OrganizationalCustomerFiltering> T setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
        return (T) this;
    }

}
