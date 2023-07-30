package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.RecurringPriceEntry;

public class RecurringPriceEntryUpdate extends RecurringPriceEntryCreate {
	private String id;
	@JsonIgnore
	private RecurringPriceEntry recurringPriceEntry;

	public String getId() {
		return id;
	}

	public RecurringPriceEntryUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public RecurringPriceEntry getRecurringPriceEntry() {
		return recurringPriceEntry;
	}

	public RecurringPriceEntryUpdate setRecurringPriceEntry(RecurringPriceEntry recurringPriceEntry) {
		this.recurringPriceEntry = recurringPriceEntry;
		return this;
	}
}
