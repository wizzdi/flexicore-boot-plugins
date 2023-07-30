package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.flexicore.organization.model.Organization;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BranchFiltering extends SiteFiltering {

	private Set<String> organizationIds = new HashSet<>();
	@JsonIgnore
	@TypeRetention(Organization.class)
	private List<Organization> organizations;

	public Set<String> getOrganizationIds() {
		return organizationIds;
	}

	public BranchFiltering setOrganizationIds(Set<String> organizationIds) {
		this.organizationIds = organizationIds;
		return this;
	}

	@JsonIgnore
	public List<Organization> getOrganizations() {
		return organizations;
	}

	public BranchFiltering setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
		return this;
	}
}
