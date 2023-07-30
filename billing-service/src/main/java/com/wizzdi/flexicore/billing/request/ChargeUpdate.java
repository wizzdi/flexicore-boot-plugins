package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.billing.Charge;

public class ChargeUpdate extends ChargeCreate {
	private String id;
	@JsonIgnore
	private Charge charge;

	public String getId() {
		return id;
	}

	public ChargeUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Charge getCharge() {
		return charge;
	}

	public ChargeUpdate setCharge(Charge charge) {
		this.charge = charge;
		return this;
	}
}
