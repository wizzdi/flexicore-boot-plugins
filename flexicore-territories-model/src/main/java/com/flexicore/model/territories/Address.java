package com.flexicore.model.territories;

import com.flexicore.model.Baseclass;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Address extends Baseclass {



	private int floorForAddress;
	@ManyToOne(targetEntity = Street.class)
	private Street street;

	@ManyToOne(targetEntity = Neighbourhood.class)
	private Neighbourhood neighbourhood;
	private String zipCode;
	private String houseNumber;
	private String externalId;

	public Address() {
	}

	public int getFloorForAddress() {
		return floorForAddress;
	}

	public void setFloorForAddress(int floorForAddress) {
		this.floorForAddress = floorForAddress;
	}

	@ManyToOne(targetEntity = Street.class)
	public Street getStreet() {
		return street;
	}

	public void setStreet(Street street) {
		this.street = street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	@ManyToOne(targetEntity = Neighbourhood.class)
	public Neighbourhood getNeighbourhood() {
		return neighbourhood;
	}

	public Address setNeighbourhood(Neighbourhood neighbourhood) {
		this.neighbourhood = neighbourhood;
		return this;
	}

	public String getExternalId() {
		return externalId;
	}

	public <T extends Address> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}

	public String getZipCode() {
		return zipCode;
	}

	public <T extends Address> T setZipCode(String zipCode) {
		this.zipCode = zipCode;
		return (T) this;
	}

}
