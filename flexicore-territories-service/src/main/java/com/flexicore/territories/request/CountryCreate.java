package com.flexicore.territories.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

public class CountryCreate extends BasicCreate {

	private String countryCode;

	public String getCountryCode() {
		return countryCode;
	}

	public <T extends CountryCreate> T setCountryCode(
			String countryCode) {
		this.countryCode = countryCode;
		return (T) this;
	}
}