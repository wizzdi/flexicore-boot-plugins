package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;

@Entity
public class SalesRegion extends Baseclass {

	public SalesRegion() {
	}

	public SalesRegion(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}
}
