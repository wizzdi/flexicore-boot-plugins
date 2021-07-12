package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethod;

public class PaymentMethodUpdate extends PaymentMethodCreate {
	private String id;
	@JsonIgnore
	private PaymentMethod paymentMethod;

	public String getId() {
		return id;
	}

	public PaymentMethodUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public PaymentMethodUpdate setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
		return this;
	}
}
