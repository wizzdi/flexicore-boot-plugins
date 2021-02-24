package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Street;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class AddressCreate extends BasicCreate {

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

	public <T extends AddressCreate> T setFloor(Integer floor) {
		this.floor = floor;
		return (T) this;
	}

	public String getStreetId() {
		return streetId;
	}

	public <T extends AddressCreate> T setStreetId(String streetId) {
		this.streetId = streetId;
		return (T) this;
	}

	@JsonIgnore
	public Street getStreet() {
		return street;
	}

	public <T extends AddressCreate> T setStreet(Street street) {
		this.street = street;
		return (T) this;
	}

	public Integer getNumber() {
		return number;
	}

	public <T extends AddressCreate> T setNumber(Integer number) {
		this.number = number;
		return (T) this;
	}

	public String getNeighbourhoodId() {
		return neighbourhoodId;
	}

	public <T extends AddressCreate> T setNeighbourhoodId(String neighbourhoodId) {
		this.neighbourhoodId = neighbourhoodId;
		return (T) this;
	}

	@JsonIgnore
	public Neighbourhood getNeighbourhood() {
		return neighbourhood;
	}

	public <T extends AddressCreate> T setNeighbourhood(Neighbourhood neighbourhood) {
		this.neighbourhood = neighbourhood;
		return (T) this;
	}

	public String getZipCode() {
		return zipCode;
	}

	public <T extends AddressCreate> T setZipCode(String zipCode) {
		this.zipCode = zipCode;
		return (T) this;
	}

	public String getExternalId() {
		return externalId;
	}

	public <T extends AddressCreate> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}
}