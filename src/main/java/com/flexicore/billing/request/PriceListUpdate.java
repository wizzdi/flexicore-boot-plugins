package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.PriceList;

public class PriceListUpdate extends PriceListCreate {
	private String id;
	@JsonIgnore
	private PriceList priceList;

	public String getId() {
		return id;
	}

	public PriceListUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public PriceList getPriceList() {
		return priceList;
	}

	public PriceListUpdate setPriceList(PriceList priceList) {
		this.priceList = priceList;
		return this;
	}
}
