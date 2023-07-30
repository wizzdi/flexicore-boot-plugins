package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Organization;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmployeeFiltering extends PaginationFilter {

	private BasicPropertiesFilter basicPropertiesFilter;
	private Set<String> organizationsIds=new HashSet<>();
	@JsonIgnore
	private List<Organization> organizations;
	private Set<String> externalIds;

	public Set<String> getExternalIds() {
		return externalIds;
	}

	public EmployeeFiltering setExternalIds(Set<String> externalIds) {
		this.externalIds = externalIds;
		return this;
	}

	public BasicPropertiesFilter getBasicPropertiesFilter() {
		return basicPropertiesFilter;
	}

	public <T extends EmployeeFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
		this.basicPropertiesFilter = basicPropertiesFilter;
		return (T) this;
	}

	public Set<String> getOrganizationsIds() {
		return organizationsIds;
	}

	public <T extends EmployeeFiltering> T setOrganizationsIds(Set<String> organizationsIds) {
		this.organizationsIds = organizationsIds;
		return (T) this;
	}

	@JsonIgnore
	public List<Organization> getOrganizations() {
		return organizations;
	}

	public <T extends EmployeeFiltering> T setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
		return (T) this;
	}
}
