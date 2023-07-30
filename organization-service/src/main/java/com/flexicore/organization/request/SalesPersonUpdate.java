package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.SalesPerson;

public class SalesPersonUpdate extends SalesPersonCreate {

	private String id;
	@JsonIgnore
	private SalesPerson salesPerson;

	public String getId() {
		return id;
	}

	public SalesPersonUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public SalesPerson getSalesPerson() {
		return salesPerson;
	}

	public SalesPersonUpdate setSalesPerson(SalesPerson salesPerson) {
		this.salesPerson = salesPerson;
		return this;
	}
}
