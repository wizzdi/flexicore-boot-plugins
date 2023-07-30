package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Organization;

public class OrganizationUpdate extends OrganizationCreate {

	private String id;
	@JsonIgnore
	private Organization organization;

	public String getId() {
		return id;
	}

	public OrganizationUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Organization getOrganization() {
		return organization;
	}

	public OrganizationUpdate setOrganization(Organization organization) {
		this.organization = organization;
		return this;
	}
}
