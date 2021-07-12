package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethodType;

public class PaymentMethodTypeUpdate extends PaymentMethodTypeCreate {
	private String id;
	@JsonIgnore
	private PaymentMethodType paymentMethodType;

	public String getId() {
		return id;
	}

	public PaymentMethodTypeUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public PaymentMethodType getPaymentMethodType() {
		return paymentMethodType;
	}

	public PaymentMethodTypeUpdate setPaymentMethodType(PaymentMethodType paymentMethodType) {
		this.paymentMethodType = paymentMethodType;
		return this;
	}
}
