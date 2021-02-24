package com.flexicore.territories.request;

import com.flexicore.model.territories.Street;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class StreetUpdate extends StreetCreate {

	private String id;
	@JsonIgnore
	private Street Street;

	public String getId() {
		return id;
	}

	public <T extends StreetUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public Street getStreet() {
		return Street;
	}

	public <T extends StreetUpdate> T setStreet(Street street) {
		Street = street;
		return (T) this;
	}
}