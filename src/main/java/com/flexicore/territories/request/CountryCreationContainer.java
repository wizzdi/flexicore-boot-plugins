package com.flexicore.territories.request;

import com.flexicore.request.BaseclassCreate;

public class CountryCreationContainer extends BaseclassCreate {

	private String countryCode;

	public String getCountryCode() {
		return countryCode;
	}

	public <T extends CountryCreationContainer> T setCountryCode(
			String countryCode) {
		this.countryCode = countryCode;
		return (T) this;
	}
}