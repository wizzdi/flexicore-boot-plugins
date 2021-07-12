package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.payment.InvoiceItem;

public class InvoiceItemUpdate extends InvoiceItemCreate {
	private String id;
	@JsonIgnore
	private InvoiceItem invoiceItem;

	public String getId() {
		return id;
	}

	public InvoiceItemUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public InvoiceItem getInvoiceItem() {
		return invoiceItem;
	}

	public InvoiceItemUpdate setInvoiceItem(InvoiceItem invoiceItem) {
		this.invoiceItem = invoiceItem;
		return this;
	}
}
