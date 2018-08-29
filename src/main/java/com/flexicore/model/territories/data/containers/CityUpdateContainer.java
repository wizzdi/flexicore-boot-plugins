package com.flexicore.model.territories.data.containers;

import com.flexicore.model.territories.City;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Country;

public class CityUpdateContainer {

	private String id;
	@JsonIgnore
	private City City;
	private String countryId;
	@JsonIgnore
	private Country country;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonIgnore
	public City getCity() {
		return City;
	}

	public void setCity(City City) {
		this.City = City;
	}

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