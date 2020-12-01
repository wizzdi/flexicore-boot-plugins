package com.flexicore.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer extends Baseclass {

	private String externalId;

	@JsonIgnore
	@OneToMany(targetEntity = CustomerDocument.class,mappedBy = "customer")
	private List<CustomerDocument> documents=new ArrayList<>();

	@JsonIgnore
	@OneToMany(targetEntity = IndustryToCustomer.class,mappedBy = "customer")
	private List<IndustryToCustomer> industryToCustomers=new ArrayList<>();

	public Customer() {
	}

	public Customer(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}


	@JsonIgnore
	@OneToMany(targetEntity = CustomerDocument.class,mappedBy = "customer")
	public List<CustomerDocument> getDocuments() {
		return documents;
	}

	public <T extends Customer> T setDocuments(List<CustomerDocument> documents) {
		this.documents = documents;
		return (T) this;
	}

	@JsonIgnore
	@OneToMany(targetEntity = IndustryToCustomer.class,mappedBy = "customer")
	public List<IndustryToCustomer> getIndustryToCustomers() {
		return industryToCustomers;
	}

	public <T extends Customer> T setIndustryToCustomers(List<IndustryToCustomer> industryToCustomers) {
		this.industryToCustomers = industryToCustomers;
		return (T) this;
	}

	public String getExternalId() {
		return externalId;
	}

	public <T extends Customer> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}
}
