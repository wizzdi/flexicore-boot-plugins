package com.flexicore.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer extends SecuredBasic {

	private String externalId;

	@Column(columnDefinition = "timestamp with time zone")
	private OffsetDateTime lastLogin;

	@JsonIgnore
	@OneToMany(targetEntity = CustomerDocument.class,mappedBy = "customer")
	private List<CustomerDocument> documents=new ArrayList<>();

	@JsonIgnore
	@OneToMany(targetEntity = IndustryToCustomer.class,mappedBy = "customer")
	private List<IndustryToCustomer> industryToCustomers=new ArrayList<>();


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

	@Column(columnDefinition = "timestamp with time zone")
	public OffsetDateTime getLastLogin() {
		return lastLogin;
	}

	public <T extends Customer> T setLastLogin(OffsetDateTime lastUsed) {
		this.lastLogin = lastUsed;
		return (T) this;
	}
}
