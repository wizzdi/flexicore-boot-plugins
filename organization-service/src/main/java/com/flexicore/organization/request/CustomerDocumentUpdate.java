package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.CustomerDocument;

public class CustomerDocumentUpdate extends CustomerDocumentCreate {

	private String id;
	@JsonIgnore
	private CustomerDocument customerDocument;

	public String getId() {
		return id;
	}

	public CustomerDocumentUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public CustomerDocument getCustomerDocument() {
		return customerDocument;
	}

	public CustomerDocumentUpdate setCustomerDocument(CustomerDocument customerDocument) {
		this.customerDocument = customerDocument;
		return this;
	}
}
