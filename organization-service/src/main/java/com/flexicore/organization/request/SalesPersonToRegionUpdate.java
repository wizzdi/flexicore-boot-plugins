package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.SalesPersonToRegion;

public class SalesPersonToRegionUpdate extends SalesPersonToRegionCreate {

	private String id;
	@JsonIgnore
	private SalesPersonToRegion salesPersonToRegion;

	public String getId() {
		return id;
	}

	public SalesPersonToRegionUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public SalesPersonToRegion getSalesPersonToRegion() {
		return salesPersonToRegion;
	}

	public SalesPersonToRegionUpdate setSalesPersonToRegion(SalesPersonToRegion salesPersonToRegion) {
		this.salesPersonToRegion = salesPersonToRegion;
		return this;
	}
}
