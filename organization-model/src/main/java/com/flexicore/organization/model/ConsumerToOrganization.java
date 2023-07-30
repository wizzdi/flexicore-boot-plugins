package com.flexicore.organization.model;

import com.flexicore.model.SecuredBasic;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class ConsumerToOrganization extends SecuredBasic {

	@ManyToOne(targetEntity = Consumer.class)
	private Consumer consumer;
	@ManyToOne(targetEntity = Organization.class)
	private Organization organization;

	@ManyToOne(targetEntity = Consumer.class)
	public Consumer getConsumer() {
		return consumer;
	}

	public <T extends ConsumerToOrganization> T setConsumer(Consumer consumer) {
		this.consumer = consumer;
		return (T) this;
	}

	@ManyToOne(targetEntity = Organization.class)
	public Organization getOrganization() {
		return organization;
	}

	public <T extends ConsumerToOrganization> T setOrganization(Organization organization) {
		this.organization = organization;
		return (T) this;
	}
}
