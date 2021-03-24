package com.flexicore.organization.model;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class SalesPersonToRegion extends SecuredBasic {


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
