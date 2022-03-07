package com.flexicore.organization.model;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Employee extends SecuredBasic {


	@ManyToOne(targetEntity = Organization.class)
	private Organization organization;
	private String externalId;
	@ManyToOne(targetEntity = Organization.class)
	public Organization getOrganization() {
		return organization;
	}

	public Employee setOrganization(Organization organization) {
		this.organization = organization;
		return this;
	}

	public String getExternalId() {
		return externalId;
	}

	public Employee setExternalId(String externalId) {
		this.externalId = externalId;
		return this;
	}


}
