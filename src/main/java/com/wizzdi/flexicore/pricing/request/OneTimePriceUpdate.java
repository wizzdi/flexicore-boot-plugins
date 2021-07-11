package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.OneTimePrice;

public class OneTimePriceUpdate extends OneTimePriceCreate {
	private String id;
	@JsonIgnore
	private OneTimePrice oneTimePrice;

	public String getId() {
		return id;
	}

	public OneTimePriceUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public OneTimePrice getOneTimePrice() {
		return oneTimePrice;
	}

	public OneTimePriceUpdate setOneTimePrice(OneTimePrice oneTimePrice) {
		this.oneTimePrice = oneTimePrice;
		return this;
	}
}
