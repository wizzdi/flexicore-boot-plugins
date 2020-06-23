package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;

@Entity
public class Customer extends Organization {

	public Customer() {
	}

	public Customer(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}
}
