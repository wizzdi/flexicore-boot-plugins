package com.flexicore.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SalesPerson extends Employee {

	public SalesPerson() {
	}

	public SalesPerson(String name, SecurityContext securityContext) {
		super(name, securityContext);
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
