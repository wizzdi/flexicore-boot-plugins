package com.flexicore.model.territories.data.containers;

import com.flexicore.model.territories.Country;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CountryUpdateContainer {

	private String id;
	@JsonIgnore
	private Country Country;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonIgnore
	public Country getCountry() {
		return Country;
	}

	public void setCountry(Country Country) {
		this.Country = Country;
	}
}