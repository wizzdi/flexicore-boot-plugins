package com.flexicore.model.territories.data.containers;

import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Street;
import com.flexicore.model.territories.Zip;

public class ZipToStreetFiltering extends FilteringInformationHolder {

	private String leftsideId;
	@JsonIgnore
	private Zip leftside;
	private String rightsideId;
	@JsonIgnore
	private Street rightside;

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