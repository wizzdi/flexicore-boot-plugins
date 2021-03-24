package com.flexicore.organization.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Supplier extends Organization {

	@ManyToOne(targetEntity = SupplierApi.class)
	private SupplierApi supplierApi;


	@ManyToOne(targetEntity = SupplierApi.class)
	public SupplierApi getSupplierApi() {
		return supplierApi;
	}

	public <T extends Supplier> T setSupplierApi(SupplierApi supplierApi) {
		this.supplierApi = supplierApi;
		return (T) this;
	}
}
