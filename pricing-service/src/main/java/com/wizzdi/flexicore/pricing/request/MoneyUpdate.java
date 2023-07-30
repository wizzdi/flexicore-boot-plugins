package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.Money;

public class MoneyUpdate extends MoneyCreate {
	private String id;
	@JsonIgnore
	private Money money;

	public String getId() {
		return id;
	}

	public MoneyUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Money getMoney() {
		return money;
	}

	public MoneyUpdate setMoney(Money money) {
		this.money = money;
		return this;
	}
}
