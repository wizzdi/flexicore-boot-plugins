package com.flexicore.model.territories.data.containers;

import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Country;

public class CityFiltering extends FilteringInformationHolder {

	private String countryId;
	@JsonIgnore
	private Country country;

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	@JsonIgnore
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
}