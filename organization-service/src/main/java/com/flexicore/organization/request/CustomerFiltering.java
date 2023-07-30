package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.flexicore.model.SecurityUser;
import com.flexicore.organization.model.Organization;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomerFiltering extends PaginationFilter {

	private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> externalIds;
    @JsonIgnore
    @TypeRetention(SecurityUser.class)
    private List<SecurityUser> users;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends CustomerFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getExternalIds() {
        return externalIds;
    }

    public <T extends CustomerFiltering> T setExternalIds(Set<String> externalIds) {
        this.externalIds = externalIds;
        return (T) this;
    }

    @JsonIgnore
    public List<SecurityUser> getUsers() {
        return users;
    }

    public <T extends CustomerFiltering> T setUsers(List<SecurityUser> users) {
        this.users = users;
        return (T) this;
    }
}
