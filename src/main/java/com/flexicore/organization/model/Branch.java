package com.flexicore.organization.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Branch extends Site {


	@ManyToOne(targetEntity = Organization.class)
	private Organization organization;

	@ManyToOne(targetEntity = Organization.class)
	public Organization getOrganization() {
		return organization;
	}

	public Branch setOrganization(Organization organization) {
		this.organization = organization;
		return this;
	}
}