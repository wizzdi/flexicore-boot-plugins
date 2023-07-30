package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Customer;

public class CustomerUpdate extends CustomerCreate {
	private String id;
	@JsonIgnore
	private Customer customer;

	public String getId() {
		return id;
	}

	public CustomerUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Customer getCustomer() {
		return customer;
	}

	public CustomerUpdate setCustomer(Customer customer) {
		this.customer = customer;
		return this;
	}
}
