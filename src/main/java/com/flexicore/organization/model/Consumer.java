package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.User;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;

@Entity
public class Consumer extends User {

	public Consumer() {
	}

	public Consumer(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}
}
