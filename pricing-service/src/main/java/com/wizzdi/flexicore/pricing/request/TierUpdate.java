package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.Tier;

public class TierUpdate extends TierCreate {
	private String id;
	@JsonIgnore
	private Tier tier;

	public String getId() {
		return id;
	}

	public TierUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Tier getTier() {
		return tier;
	}

	public TierUpdate setTier(Tier tier) {
		this.tier = tier;
		return this;
	}
}
