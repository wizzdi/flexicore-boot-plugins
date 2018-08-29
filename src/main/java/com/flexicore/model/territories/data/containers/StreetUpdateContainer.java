package com.flexicore.model.territories.data.containers;

import com.flexicore.model.territories.City;
import com.flexicore.model.territories.Street;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class StreetUpdateContainer {

	private String id;
	@JsonIgnore
	private Street Street;
	private String cityId;
	@JsonIgnore
	private City city;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonIgnore
	public Street getStreet() {
		return Street;
	}

	public void setStreet(Street Street) {
		this.Street = Street;
	}

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

	public void setCity(City city) {
		this.city = city;
	}
}