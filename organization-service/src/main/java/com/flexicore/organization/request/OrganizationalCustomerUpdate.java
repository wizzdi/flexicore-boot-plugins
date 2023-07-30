package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.OrganizationalCustomer;

public class OrganizationalCustomerUpdate extends OrganizationalCustomerCreate {
	private String id;
	@JsonIgnore
	private OrganizationalCustomer organizationalCustomer;

	public String getId() {
		return id;
	}

	public OrganizationalCustomerUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public OrganizationalCustomer getOrganizationalCustomer() {
		return organizationalCustomer;
	}

	public OrganizationalCustomerUpdate setOrganizationalCustomer(OrganizationalCustomer organizationalCustomer) {
		this.organizationalCustomer = organizationalCustomer;
		return this;
	}
}
