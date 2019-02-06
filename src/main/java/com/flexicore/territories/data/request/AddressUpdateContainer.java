package com.flexicore.territories.data.request;

import com.flexicore.model.territories.Address;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AddressUpdateContainer extends AddressCreationContainer{

	private String id;
	@JsonIgnore
	private Address Address;


	public String getId() {
		return id;
	}

	public AddressUpdateContainer setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Address getAddress() {
		return Address;
	}

	public AddressUpdateContainer setAddress(Address address) {
		Address = address;
		return this;
	}
}