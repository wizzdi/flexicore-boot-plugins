package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Street;
import com.flexicore.request.BaseclassCreate;

public class AddressCreationContainer extends BaseclassCreate {

	private Integer floor;
	private String streetId;
	@JsonIgnore
	private Street street;
	private Integer number;
	private String neighbourhoodId;
	@JsonIgnore
	private Neighbourhood neighbourhood;
	private String zipCode;
	private String externalId;

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

	public String getNeighbourhoodId() {
		return neighbourhoodId;
	}

	public AddressCreationContainer setNeighbourhoodId(String neighbourhoodId) {
		this.neighbourhoodId = neighbourhoodId;
		return this;
	}

	@JsonIgnore
	public Neighbourhood getNeighbourhood() {
		return neighbourhood;
	}

	public AddressCreationContainer setNeighbourhood(Neighbourhood neighbourhood) {
		this.neighbourhood = neighbourhood;
		return this;
	}

	public String getZipCode() {
		return zipCode;
	}

	public <T extends AddressCreationContainer> T setZipCode(String zipCode) {
		this.zipCode = zipCode;
		return (T) this;
	}

	public String getExternalId() {
		return externalId;
	}

	public <T extends AddressCreationContainer> T setExternalId(
			String externalId) {
		this.externalId = externalId;
		return (T) this;
	}
}