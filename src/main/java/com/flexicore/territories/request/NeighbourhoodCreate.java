package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.City;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class NeighbourhoodCreate extends BasicCreate {

	private String externalId;
	private String cityId;
	@JsonIgnore
	private City city;


	public String getExternalId() {
		return externalId;
	}

	public <T extends NeighbourhoodCreate> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}

	public String getCityId() {
		return cityId;
	}

	public <T extends NeighbourhoodCreate> T setCityId(String cityId) {
		this.cityId = cityId;
		return (T) this;
	}

	@JsonIgnore
	public City getCity() {
		return city;
	}

	public <T extends NeighbourhoodCreate> T setCity(City city) {
		this.city = city;
		return (T) this;
	}
}