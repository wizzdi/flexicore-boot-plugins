package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.PricingScheme;

public class PricingSchemeUpdate extends PricingSchemeCreate {
	private String id;
	@JsonIgnore
	private PricingScheme pricingScheme;

	public String getId() {
		return id;
	}

	public PricingSchemeUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public PricingScheme getPricingScheme() {
		return pricingScheme;
	}

	public PricingSchemeUpdate setPricingScheme(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
		return this;
	}
}
