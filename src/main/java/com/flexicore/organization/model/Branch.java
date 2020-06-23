package com.flexicore.organization.model;

import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Branch extends Site {

	public Branch() {
	}

	public Branch(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

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