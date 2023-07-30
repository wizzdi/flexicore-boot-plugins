package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Site;

public class SiteUpdate extends SiteCreate {

	private String id;
	@JsonIgnore
	private Site site;

	public String getId() {
		return id;
	}

	public SiteUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Site getSite() {
		return site;
	}

	public SiteUpdate setSite(Site site) {
		this.site = site;
		return this;
	}
}
