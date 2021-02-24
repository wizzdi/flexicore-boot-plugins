package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.State;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class CityCreate extends BasicCreate {

	private String externalId;
	private String countryId;
	@JsonIgnore
	private Country country;
	private String stateId;
	@JsonIgnore
	private State state;


	public String getExternalId() {
		return externalId;
	}

	public <T extends CityCreate> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}

	public String getCountryId() {
		return countryId;
	}

	public <T extends CityCreate> T setCountryId(String countryId) {
		this.countryId = countryId;
		return (T) this;
	}

	@JsonIgnore
	public Country getCountry() {
		return country;
	}

	public <T extends CityCreate> T setCountry(Country country) {
		this.country = country;
		return (T) this;
	}

	public String getStateId() {
		return stateId;
	}

	public <T extends CityCreate> T setStateId(String stateId) {
		this.stateId = stateId;
		return (T) this;
	}

	@JsonIgnore
	public State getState() {
		return state;
	}

	public <T extends CityCreate> T setState(State state) {
		this.state = state;
		return (T) this;
	}
}