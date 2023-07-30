package com.flexicore.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SalesPerson extends Employee {


	@OneToMany(targetEntity = SalesPersonToRegion.class, mappedBy = "salesPerson")
	@JsonIgnore
	private List<SalesPersonToRegion> salesPersonToRegions = new ArrayList<>();

	@OneToMany(targetEntity = SalesPersonToRegion.class, mappedBy = "salesPerson")
	@JsonIgnore
	public List<SalesPersonToRegion> getSalesPersonToRegions() {
		return salesPersonToRegions;
	}

	public <T extends SalesPerson> T setSalesPersonToRegions(List<SalesPersonToRegion> salesPersonToRegions) {
		this.salesPersonToRegions = salesPersonToRegions;
		return (T) this;
	}
}
