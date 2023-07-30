package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Supplier;

public class SupplierUpdate extends SupplierCreate {

	private String id;
	@JsonIgnore
	private Supplier supplier;

	public String getId() {
		return id;
	}

	public <T extends SupplierUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public Supplier getSupplier() {
		return supplier;
	}

	public <T extends SupplierUpdate> T setSupplier(Supplier supplier) {
		this.supplier = supplier;
		return (T) this;
	}
}
