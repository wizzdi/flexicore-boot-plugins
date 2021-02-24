package com.flexicore.territories.request;

import com.flexicore.model.territories.Address;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AddressUpdate extends AddressCreate {

	private String id;
	@JsonIgnore
	private Address Address;

	public String getId() {
		return id;
	}

	public AddressUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Address getAddress() {
		return Address;
	}

	public AddressUpdate setAddress(Address address) {
		Address = address;
		return this;
	}
}