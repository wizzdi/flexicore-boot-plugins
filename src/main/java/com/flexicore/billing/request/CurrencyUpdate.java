package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.Currency;

public class CurrencyUpdate extends CurrencyCreate {
	private String id;
	@JsonIgnore
	private Currency currency;

	public String getId() {
		return id;
	}

	public CurrencyUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Currency getCurrency() {
		return currency;
	}

	public CurrencyUpdate setCurrency(Currency currency) {
		this.currency = currency;
		return this;
	}
}
