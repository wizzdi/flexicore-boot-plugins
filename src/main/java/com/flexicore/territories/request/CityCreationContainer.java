package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.State;
import com.flexicore.request.BaseclassCreate;

public class CityCreationContainer extends BaseclassCreate {

	private String externalId;

	private String countryId;
	@JsonIgnore
	private Country country;

	private String stateId;
	@JsonIgnore
	private State state;

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



	public String getExternalId() {
		return externalId;
	}

	public <T extends CityCreationContainer> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}

	public String getStateId() {
		return stateId;
	}

	public <T extends CityCreationContainer> T setStateId(String stateId) {
		this.stateId = stateId;
		return (T) this;
	}

	@JsonIgnore
	public State getState() {
		return state;
	}

	public <T extends CityCreationContainer> T setState(State state) {
		this.state = state;
		return (T) this;
	}
}