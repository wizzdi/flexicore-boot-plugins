package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.model.territories.City;

import java.util.Set;

public class NeighbourhoodFiltering extends FilteringInformationHolder {

	private Set<String> externalIds;
	private String cityId;
	@JsonIgnore
	private City city;

	public Set<String> getExternalIds() {
		return externalIds;
	}

	public <T extends NeighbourhoodFiltering> T setExternalIds(
			Set<String> externalIds) {
		this.externalIds = externalIds;
		return (T) this;
	}

	public String getCityId() {
		return cityId;
	}

	public NeighbourhoodFiltering setCityId(String cityId) {
		this.cityId = cityId;
		return this;
	}

	@JsonIgnore
	public City getCity() {
		return city;
	}

	public NeighbourhoodFiltering setCity(City city) {
		this.city = city;
		return this;
	}

}