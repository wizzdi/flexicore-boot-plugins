package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.SupplierApi;

public class SupplierApiUpdate extends SupplierApiCreate {

	private String id;
	@JsonIgnore
	private SupplierApi supplierApi;

	public String getId() {
		return id;
	}

	public <T extends SupplierApiUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public SupplierApi getSupplierApi() {
		return supplierApi;
	}

	public <T extends SupplierApiUpdate> T setSupplierApi(
			SupplierApi supplierApi) {
		this.supplierApi = supplierApi;
		return (T) this;
	}
}
