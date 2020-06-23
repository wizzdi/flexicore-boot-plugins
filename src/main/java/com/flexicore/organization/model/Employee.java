package com.flexicore.organization.model;

import com.flexicore.model.User;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Employee extends User {

	public Employee() {
	}

	public Employee(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	@ManyToOne(targetEntity = Organization.class)
	private Organization organization;

	@ManyToOne(targetEntity = Organization.class)
	public Organization getOrganization() {
		return organization;
	}

	public Employee setOrganization(Organization organization) {
		this.organization = organization;
		return this;
	}
}
