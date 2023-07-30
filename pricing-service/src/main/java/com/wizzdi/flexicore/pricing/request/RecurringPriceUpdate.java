package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.RecurringPrice;

public class RecurringPriceUpdate extends RecurringPriceCreate {
	private String id;
	@JsonIgnore
	private RecurringPrice recurringPrice;

	public String getId() {
		return id;
	}

	public RecurringPriceUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public RecurringPrice getRecurringPrice() {
		return recurringPrice;
	}

	public RecurringPriceUpdate setRecurringPrice(RecurringPrice recurringPrice) {
		this.recurringPrice = recurringPrice;
		return this;
	}
}
