package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.billing.ChargeReference;

public class ChargeReferenceUpdate extends ChargeReferenceCreate {
	private String id;
	@JsonIgnore
	private ChargeReference chargeReferenceObject;

	public String getId() {
		return id;
	}

	public ChargeReferenceUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public ChargeReference getChargeReferenceObject() {
		return chargeReferenceObject;
	}

	public <T extends ChargeReferenceUpdate> T setChargeReferenceObject(ChargeReference chargeReferenceObject) {
		this.chargeReferenceObject = chargeReferenceObject;
		return (T) this;
	}
}
