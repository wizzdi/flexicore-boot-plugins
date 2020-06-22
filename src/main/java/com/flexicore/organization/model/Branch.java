package com.flexicore.organization.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Branch extends Site {
	static private Branch s_Singleton = new Branch();
	static public Branch s() {
		return s_Singleton;
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