package com.flexicore.territories.request;

import com.flexicore.model.FilteringInformationHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.State;

public class CityFiltering extends FilteringInformationHolder {



	private String countryId;
	@JsonIgnore
	private Country country;

	private  String stateId;
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

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getStateId() {
		return stateId;
	}

	public <T extends CityFiltering> T setStateId(String stateId) {
		this.stateId = stateId;
		return (T) this;
	}

	@JsonIgnore
	public State getState() {
		return state;
	}

	public <T extends CityFiltering> T setState(State state) {
		this.state = state;
		return (T) this;
	}
}