package com.flexicore.territories.request;

import com.flexicore.model.territories.Country;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CountryUpdateContainer extends CountryCreationContainer {

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