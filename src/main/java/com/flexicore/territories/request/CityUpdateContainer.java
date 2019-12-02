package com.flexicore.territories.request;

import com.flexicore.model.territories.City;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CityUpdateContainer extends CityCreationContainer{

	private String id;
	@JsonIgnore
	private City City;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonIgnore
	public City getCity() {
		return City;
	}

	public void setCity(City City) {
		this.City = City;
	}

}