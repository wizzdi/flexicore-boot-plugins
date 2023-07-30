package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.SalesPerson;
import com.flexicore.organization.model.SalesRegion;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.OffsetDateTime;

public class SalesPersonToRegionCreate extends BasicCreate {

	private String salesPersonId;
	@JsonIgnore
	private SalesPerson salesPerson;
	private String salesRegionId;
	@JsonIgnore
	private SalesRegion salesRegion;
	private OffsetDateTime startTime;
	private OffsetDateTime endTime;

	public String getSalesPersonId() {
		return salesPersonId;
	}

	public SalesPersonToRegionCreate setSalesPersonId(String salesPersonId) {
		this.salesPersonId = salesPersonId;
		return this;
	}

	@JsonIgnore
	public SalesPerson getSalesPerson() {
		return salesPerson;
	}

	public SalesPersonToRegionCreate setSalesPerson(SalesPerson salesPerson) {
		this.salesPerson = salesPerson;
		return this;
	}

	public String getSalesRegionId() {
		return salesRegionId;
	}

	public SalesPersonToRegionCreate setSalesRegionId(String salesRegionId) {
		this.salesRegionId = salesRegionId;
		return this;
	}

	@JsonIgnore
	public SalesRegion getSalesRegion() {
		return salesRegion;
	}

	public SalesPersonToRegionCreate setSalesRegion(SalesRegion salesRegion) {
		this.salesRegion = salesRegion;
		return this;
	}

	public OffsetDateTime getStartTime() {
		return startTime;
	}

	public SalesPersonToRegionCreate setStartTime(OffsetDateTime startTime) {
		this.startTime = startTime;
		return this;
	}

	public OffsetDateTime getEndTime() {
		return endTime;
	}

	public SalesPersonToRegionCreate setEndTime(OffsetDateTime endTime) {
		this.endTime = endTime;
		return this;
	}
}
