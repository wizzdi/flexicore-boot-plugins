package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.flexicore.organization.model.Organization;
import com.flexicore.organization.model.SalesRegion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SalesPersonFiltering extends EmployeeFiltering {

	private Set<String> regionIds = new HashSet<>();
	@JsonIgnore
	@TypeRetention(SalesRegion.class)
	private List<SalesRegion> salesRegions;

	public Set<String> getRegionIds() {
		return regionIds;
	}

	public SalesPersonFiltering setRegionIds(Set<String> regionIds) {
		this.regionIds = regionIds;
		return this;
	}

	@JsonIgnore
	public List<SalesRegion> getSalesRegions() {
		return salesRegions;
	}

	public SalesPersonFiltering setSalesRegions(List<SalesRegion> salesRegions) {
		this.salesRegions = salesRegions;
		return this;
	}
}
