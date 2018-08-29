package com.flexicore.model.territories.data.containers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Street;

public class AddressCreationContainer {

	private int floor;
	private String streetId;
	@JsonIgnore
	private Street street;
	private int number;

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public String getStreetId() {
		return streetId;
	}

	public void setStreetId(String streetId) {
		this.streetId = streetId;
	}

	@JsonIgnore
	public Street getStreet() {
		return street;
	}

	public void setStreet(Street street) {
		this.street = street;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}