package com.flexicore.organization.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

public class SupplierApiCreate extends BasicCreate {

	private String implementorCanonicalName;

	public String getImplementorCanonicalName() {
		return implementorCanonicalName;
	}

	public <T extends SupplierApiCreate> T setImplementorCanonicalName(
			String implementorCanonicalName) {
		this.implementorCanonicalName = implementorCanonicalName;
		return (T) this;
	}
}
