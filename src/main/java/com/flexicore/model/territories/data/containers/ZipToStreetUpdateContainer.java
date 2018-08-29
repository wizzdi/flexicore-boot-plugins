package com.flexicore.model.territories.data.containers;

import com.flexicore.model.territories.Street;
import com.flexicore.model.territories.Zip;
import com.flexicore.model.territories.ZipToStreet;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ZipToStreetUpdateContainer {

	private String id;
	@JsonIgnore
	private ZipToStreet ZipToStreet;
	private String leftsideId;
	@JsonIgnore
	private Zip leftside;
	private String rightsideId;
	@JsonIgnore
	private Street rightside;

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

	public String getLeftsideId() {
		return leftsideId;
	}

	public void setLeftsideId(String leftsideId) {
		this.leftsideId = leftsideId;
	}

	@JsonIgnore
	public Zip getLeftside() {
		return leftside;
	}

	public void setLeftside(Zip leftside) {
		this.leftside = leftside;
	}

	public String getRightsideId() {
		return rightsideId;
	}

	public void setRightsideId(String rightsideId) {
		this.rightsideId = rightsideId;
	}

	@JsonIgnore
	public Street getRightside() {
		return rightside;
	}

	public void setRightside(Street rightside) {
		this.rightside = rightside;
	}
}