package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;

@Entity
public class SupplierApi extends Baseclass {

	public SupplierApi() {
	}

	public SupplierApi(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	private String implementorCanonicalName;

	public String getImplementorCanonicalName() {
		return implementorCanonicalName;
	}

	public <T extends SupplierApi> T setImplementorCanonicalName(
			String implementorCanonicalName) {
		this.implementorCanonicalName = implementorCanonicalName;
		return (T) this;
	}
}
