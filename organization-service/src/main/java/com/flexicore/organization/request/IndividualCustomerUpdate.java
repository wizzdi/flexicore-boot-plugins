package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.IndividualCustomer;

public class IndividualCustomerUpdate extends IndividualCustomerCreate {
	private String id;
	@JsonIgnore
	private IndividualCustomer individualCustomer;

	public String getId() {
		return id;
	}

	public IndividualCustomerUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public IndividualCustomer getIndividualCustomer() {
		return individualCustomer;
	}

	public IndividualCustomerUpdate setIndividualCustomer(IndividualCustomer individualCustomer) {
		this.individualCustomer = individualCustomer;
		return this;
	}
}
