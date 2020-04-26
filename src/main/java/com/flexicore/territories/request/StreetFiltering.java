package com.flexicore.territories.request;

import com.flexicore.model.FilteringInformationHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.City;

import java.util.Set;

public class StreetFiltering extends FilteringInformationHolder {

	private Set<String> externalIds;

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

	public Set<String> getExternalIds() {
		return externalIds;
	}

	public <T extends StreetFiltering> T setExternalIds(Set<String> externalIds) {
		this.externalIds = externalIds;
		return (T) this;
	}
}