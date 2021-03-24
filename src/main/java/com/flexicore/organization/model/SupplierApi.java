package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.SecuredBasic;
import com.flexicore.security.SecurityContextBase;

import javax.persistence.Entity;

@Entity
public class SupplierApi extends SecuredBasic {

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
