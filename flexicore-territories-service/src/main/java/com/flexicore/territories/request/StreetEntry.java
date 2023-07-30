package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StreetEntry {

	private int cityId;
	private String cityName;
	private int streetId;
	private String streetName;

	@JsonProperty("סמל_ישוב")
	public int getCityId() {
		return cityId;
	}

	public <T extends StreetEntry> T setCityId(int cityId) {
		this.cityId = cityId;
		return (T) this;
	}
	@JsonProperty("שם_ישוב")
	public String getCityName() {
		return cityName;
	}

	public <T extends StreetEntry> T setCityName(String cityName) {
		this.cityName = cityName;
		return (T) this;
	}
	@JsonProperty("סמל_רחוב")
	public int getStreetId() {
		return streetId;
	}

	public <T extends StreetEntry> T setStreetId(int streetId) {
		this.streetId = streetId;
		return (T) this;
	}
	@JsonProperty("שם_רחוב")
	public String getStreetName() {
		return streetName;
	}

	public <T extends StreetEntry> T setStreetName(String streetName) {
		this.streetName = streetName;
		return (T) this;
	}

	@Override
	public String toString() {
		return "StreetEntry{" + "cityId=" + cityId + ", cityName='" + cityName
				+ '\'' + ", streetId=" + streetId + ", streetName='"
				+ streetName + '\'' + '}';
	}
}
