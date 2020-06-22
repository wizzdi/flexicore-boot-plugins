package com.flexicore.territories.request;

public class AddressImportRequest {

	private String url;

	public String getUrl() {
		return url;
	}

	public <T extends AddressImportRequest> T setUrl(String url) {
		this.url = url;
		return (T) this;
	}
}
