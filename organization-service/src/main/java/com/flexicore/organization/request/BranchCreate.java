package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Organization;

public class BranchCreate extends SiteCreate {

	private String organizationId;
	@JsonIgnore
	private Organization organization;

	public String getOrganizationId() {
		return organizationId;
	}

	public BranchCreate setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
		return this;
	}

	@JsonIgnore
	public Organization getOrganization() {
		return organization;
	}

	public BranchCreate setOrganization(Organization organization) {
		this.organization = organization;
		return this;
	}
}
