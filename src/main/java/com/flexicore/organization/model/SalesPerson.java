package com.flexicore.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SalesPerson extends Employee {
	static SalesPerson s_Singleton = new SalesPerson();

	public static SalesPerson s() {
		return s_Singleton;
	}
	@OneToMany(targetEntity = SalesPersonToRegion.class, mappedBy = "leftside")
	@JsonIgnore
	private List<SalesPersonToRegion> salesPersonToRegions = new ArrayList<>();

	@OneToMany(targetEntity = SalesPersonToRegion.class, mappedBy = "leftside")
	@JsonIgnore
	public List<SalesPersonToRegion> getSalesPersonToRegions() {
		return salesPersonToRegions;
	}

	public SalesPerson setSalesPersonToRegions(
			List<SalesPersonToRegion> salesPersonToRegions) {
		this.salesPersonToRegions = salesPersonToRegions;
		return this;
	}
}
