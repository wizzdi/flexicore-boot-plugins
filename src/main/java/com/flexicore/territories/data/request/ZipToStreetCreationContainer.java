package com.flexicore.territories.data.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Street;
import com.flexicore.model.territories.Zip;

public class ZipToStreetCreationContainer {

	private String zipId;
	@JsonIgnore
	private Zip zip;
	private String streetId;
	@JsonIgnore
	private Street street;

	public String getZipId() {
		return zipId;
	}

	public void setZipId(String zipId) {
		this.zipId = zipId;
	}

	@JsonIgnore
	public Zip getZip() {
		return zip;
	}

	public void setZip(Zip zip) {
		this.zip = zip;
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
}