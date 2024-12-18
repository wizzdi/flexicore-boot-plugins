package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;

import jakarta.persistence.Entity;

@Entity
public class SupplierApi extends Baseclass {

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
