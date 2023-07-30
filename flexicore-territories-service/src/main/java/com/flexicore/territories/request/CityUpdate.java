package com.flexicore.territories.request;

import com.flexicore.model.territories.City;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CityUpdate extends CityCreate {

	private String id;
	@JsonIgnore
	private City City;

	public String getId() {
		return id;
	}

	public <T extends CityUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public City getCity() {
		return City;
	}

	public <T extends CityUpdate> T setCity(City city) {
		City = city;
		return (T) this;
	}
}