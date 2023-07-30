package com.flexicore.territories.request;

import com.flexicore.model.territories.Country;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CountryUpdate extends CountryCreate {

	private String id;
	@JsonIgnore
	private Country Country;

	public String getId() {
		return id;
	}

	public <T extends CountryUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public Country getCountry() {
		return Country;
	}

	public void setCountry(Country Country) {
		this.Country = Country;
	}
}