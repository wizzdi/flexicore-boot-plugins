package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Country;
import com.flexicore.request.BaseclassCreate;

public class StateCreate extends BaseclassCreate {

	private String externalId;

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

	public <T extends StateCreate> T setCountry(Country country) {
		this.country = country;
		return (T) this;
	}



	public String getExternalId() {
		return externalId;
	}

	public <T extends StateCreate> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}
}