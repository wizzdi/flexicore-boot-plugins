package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.payment.Payment;

public class PaymentUpdate extends PaymentCreate {
	private String id;
	@JsonIgnore
	private Payment payment;

	public String getId() {
		return id;
	}

	public PaymentUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Payment getPayment() {
		return payment;
	}

	public PaymentUpdate setPayment(Payment payment) {
		this.payment = payment;
		return this;
	}
}
