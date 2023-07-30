package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Address;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class SiteCreate extends BasicCreate {

	private String addressId;
	@JsonIgnore
	private Address address;
	private String externalId;

	public String getAddressId() {
		return addressId;
	}

	public SiteCreate setAddressId(String addressId) {
		this.addressId = addressId;
		return this;
	}

	@JsonIgnore
	public Address getAddress() {
		return address;
	}

	public SiteCreate setAddress(Address address) {
		this.address = address;
		return this;
	}

	public String getExternalId() {
		return externalId;
	}

	public SiteCreate setExternalId(String externalId) {
		this.externalId = externalId;
		return this;
	}
}
