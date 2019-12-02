package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Country;

public class CityCreationContainer {

	private String externalId;
	private String name;
	private String description;
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

	public <T extends CityCreationContainer> T setCountry(Country country) {
		this.country = country;
		return (T) this;
	}

	public String getName() {
		return name;
	}

	public CityCreationContainer setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public CityCreationContainer setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getExternalId() {
		return externalId;
	}

	public <T extends CityCreationContainer> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}
}