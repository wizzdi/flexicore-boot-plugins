package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Street;
import com.flexicore.model.territories.Zip;

public class AddressCreationContainer {

	private Integer floor;
	private String streetId;
	@JsonIgnore
	private Street street;
	private Integer number;
	private String neighbourhoodId;
	@JsonIgnore
	private Neighbourhood neighbourhood;
	private String name;
	private String description;
	private String zipId;
	@JsonIgnore
	private Zip zip;

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

	public String getName() {
		return name;
	}

	public AddressCreationContainer setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public AddressCreationContainer setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getZipId() {
		return zipId;
	}

	public <T extends AddressCreationContainer> T setZipId(String zipId) {
		this.zipId = zipId;
		return (T) this;
	}

	@JsonIgnore
	public Zip getZip() {
		return zip;
	}

	public <T extends AddressCreationContainer> T setZip(Zip zip) {
		this.zip = zip;
		return (T) this;
	}
}