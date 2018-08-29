package com.flexicore.model.territories.data.containers;

import com.flexicore.model.territories.Zip;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ZipUpdateContainer {

	private String id;
	@JsonIgnore
	private Zip Zip;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonIgnore
	public Zip getZip() {
		return Zip;
	}

	public void setZip(Zip Zip) {
		this.Zip = Zip;
	}
}