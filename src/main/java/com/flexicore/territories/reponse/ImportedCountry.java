package com.flexicore.territories.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImportedCountry {

	@JsonProperty("Code")
	private String code;
	@JsonProperty("Name")
	private String name;

	public String getCode() {
		return code;
	}

	public <T extends ImportedCountry> T setCode(String code) {
		this.code = code;
		return (T) this;
	}

	public String getName() {
		return name;
	}

	public <T extends ImportedCountry> T setName(String name) {
		this.name = name;
		return (T) this;
	}
}
