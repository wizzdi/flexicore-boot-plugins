package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.Price;

public class PriceUpdate extends PriceCreate {
	private String id;
	@JsonIgnore
	private Price price;

	public String getId() {
		return id;
	}

	public PriceUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Price getPrice() {
		return price;
	}

	public PriceUpdate setPrice(Price price) {
		this.price = price;
		return this;
	}
}
