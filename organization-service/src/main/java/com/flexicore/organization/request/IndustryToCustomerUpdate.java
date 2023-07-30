package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.IndustryToCustomer;

public class IndustryToCustomerUpdate extends IndustryToCustomerCreate {

	private String id;
	@JsonIgnore
	private IndustryToCustomer industryToCustomer;

	public String getId() {
		return id;
	}

	public IndustryToCustomerUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public IndustryToCustomer getIndustryToCustomer() {
		return industryToCustomer;
	}

	public IndustryToCustomerUpdate setIndustryToCustomer(IndustryToCustomer industryToCustomer) {
		this.industryToCustomer = industryToCustomer;
		return this;
	}
}
