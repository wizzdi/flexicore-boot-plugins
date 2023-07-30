package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.payment.Invoice;

public class InvoiceUpdate extends InvoiceCreate {
	private String id;
	@JsonIgnore
	private Invoice invoice;

	public String getId() {
		return id;
	}

	public InvoiceUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Invoice getInvoice() {
		return invoice;
	}

	public InvoiceUpdate setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
}
