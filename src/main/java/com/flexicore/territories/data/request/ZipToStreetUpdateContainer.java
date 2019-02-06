package com.flexicore.territories.data.request;

import com.flexicore.model.territories.ZipToStreet;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ZipToStreetUpdateContainer extends ZipToStreetCreationContainer{

	private String id;
	@JsonIgnore
	private ZipToStreet ZipToStreet;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonIgnore
	public ZipToStreet getZipToStreet() {
		return ZipToStreet;
	}

	public void setZipToStreet(ZipToStreet ZipToStreet) {
		this.ZipToStreet = ZipToStreet;
	}


}