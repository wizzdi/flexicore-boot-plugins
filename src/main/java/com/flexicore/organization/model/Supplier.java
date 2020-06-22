package com.flexicore.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Supplier extends Organization {
	static Supplier s_Singleton = new Supplier();

	public static Supplier s() {
		return s_Singleton;
	}

	@OneToMany(targetEntity = SupplierToProduct.class, mappedBy = "leftside")
	@JsonIgnore
	private List<SupplierToProduct> supplierToProducts = new ArrayList<>();

	@ManyToOne(targetEntity = SupplierApi.class)
	private SupplierApi supplierApi;

	@OneToMany(targetEntity = SupplierToProduct.class, mappedBy = "leftside")
	@JsonIgnore
	public List<SupplierToProduct> getSupplierToProducts() {
		return supplierToProducts;
	}

	public Supplier setSupplierToProducts(
			List<SupplierToProduct> supplierToProducts) {
		this.supplierToProducts = supplierToProducts;
		return this;
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
