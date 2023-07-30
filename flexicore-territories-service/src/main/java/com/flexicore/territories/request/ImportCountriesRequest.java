package com.flexicore.territories.request;

import java.util.UUID;

public class ImportCountriesRequest {

	private String id;

	public ImportCountriesRequest() {
		this.id= UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public <T extends ImportCountriesRequest> T setId(String id) {
		this.id = id;
		return (T) this;
	}
}
