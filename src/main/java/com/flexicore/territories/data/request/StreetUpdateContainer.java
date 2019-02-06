package com.flexicore.territories.data.request;

import com.flexicore.model.territories.Street;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class StreetUpdateContainer extends StreetCreationContainer{

	private String id;
	@JsonIgnore
	private Street Street;


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


}