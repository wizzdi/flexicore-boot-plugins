package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class SalesPersonToRegion extends Baseclass {


	@ManyToOne(targetEntity = SalesPerson.class)
	private SalesPerson salesPerson;
	@ManyToOne(targetEntity = SalesRegion.class)
	private SalesRegion salesRegion;

	@ManyToOne(targetEntity = SalesPerson.class)
	public SalesPerson getSalesPerson() {
		return salesPerson;
	}

	public <T extends SalesPersonToRegion> T setSalesPerson(SalesPerson salesPerson) {
		this.salesPerson = salesPerson;
		return (T) this;
	}

	@ManyToOne(targetEntity = SalesRegion.class)
	public SalesRegion getSalesRegion() {
		return salesRegion;
	}

	public <T extends SalesPersonToRegion> T setSalesRegion(SalesRegion salesRegion) {
		this.salesRegion = salesRegion;
		return (T) this;
	}
}
