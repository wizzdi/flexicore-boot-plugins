package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.PriceListItem;

public class PriceListItemUpdate extends PriceListItemCreate {
	private String id;
	@JsonIgnore
	private PriceListItem priceListItem;

	public String getId() {
		return id;
	}

	public PriceListItemUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public PriceListItem getPriceListItem() {
		return priceListItem;
	}

	public PriceListItemUpdate setPriceListItem(PriceListItem priceListItem) {
		this.priceListItem = priceListItem;
		return this;
	}
}
