package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.SalesRegion;

public class SalesRegionUpdate extends SalesRegionCreate {
	private String id;
	@JsonIgnore
	private SalesRegion salesRegion;

	public String getId() {
		return id;
	}

	public SalesRegionUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public SalesRegion getSalesRegion() {
		return salesRegion;
	}

	public SalesRegionUpdate setSalesRegion(SalesRegion salesRegion) {
		this.salesRegion = salesRegion;
		return this;
	}
}
