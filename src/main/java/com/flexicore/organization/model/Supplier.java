package com.flexicore.organization.model;

import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Supplier extends Organization {

	@ManyToOne(targetEntity = SupplierApi.class)
	private SupplierApi supplierApi;

	public Supplier() {
	}

	public Supplier(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}


	@ManyToOne(targetEntity = SupplierApi.class)
	public SupplierApi getSupplierApi() {
		return supplierApi;
	}

	public <T extends Supplier> T setSupplierApi(SupplierApi supplierApi) {
		this.supplierApi = supplierApi;
		return (T) this;
	}
}
