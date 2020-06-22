package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;

import javax.persistence.Entity;

@Entity
public class SupplierApi extends Baseclass {
	static SupplierApi s_Singleton = new SupplierApi();
	public static SupplierApi s() {
		return s_Singleton;
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
