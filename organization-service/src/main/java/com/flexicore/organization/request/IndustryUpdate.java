package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Industry;

public class IndustryUpdate extends IndustryCreate {

	private String id;
	@JsonIgnore
	private Industry industry;

	public String getId() {
		return id;
	}

	public IndustryUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Industry getIndustry() {
		return industry;
	}

	public IndustryUpdate setIndustry(Industry industry) {
		this.industry = industry;
		return this;
	}
}
