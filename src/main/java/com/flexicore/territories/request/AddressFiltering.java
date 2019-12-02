package com.flexicore.territories.request;

import com.flexicore.model.FilteringInformationHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Street;

public class AddressFiltering extends FilteringInformationHolder {

	private Integer floor;
	private String streetId;
	@JsonIgnore
	private Street street;
	private Integer number;

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
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

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}
}