package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.City;
import com.flexicore.request.BaseclassCreate;

public class StreetCreationContainer extends BaseclassCreate {

	private String externalId;

	private String cityId;
	@JsonIgnore
	private City city;

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	@JsonIgnore
	public City getCity() {
		return city;
	}

	public <T extends StreetCreationContainer> T setCity(City city) {
		this.city = city;
		return (T) this;
	}


	public String getExternalId() {
		return externalId;
	}

	public <T extends StreetCreationContainer> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}
}