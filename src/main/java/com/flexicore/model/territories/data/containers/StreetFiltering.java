package com.flexicore.model.territories.data.containers;

import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.City;

public class StreetFiltering extends FilteringInformationHolder {

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

	public void setCity(City city) {
		this.city = city;
	}
}