package com.flexicore.organization.model;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "employee", indexes = {@Index(name = "externalid_ix", columnList = "externalid", unique = false)
,@Index(name = "externalId2_ix", columnList = "externalid2", unique = false)})
@Entity
public class Employee extends SecuredBasic {

	private String externalId;
	private String externalId2;
	@ManyToOne(targetEntity = Organization.class)
	private Organization organization;
	boolean organizationAdmin;
	@ManyToOne(targetEntity = Organization.class)
	public Organization getOrganization() {
		return organization;
	}

	public Employee setOrganization(Organization organization) {
		this.organization = organization;
		return this;
	}

	public String getExternalId() {
		return externalId;
	}

	public Employee setExternalId(String externalId) {
		this.externalId = externalId;
		return this;
	}
	public boolean isOrganizationAdmin() {
		return organizationAdmin;
	}

	public Employee setOrganizationAdmin(boolean organizationAdmin) {
		this.organizationAdmin = organizationAdmin;
		return this;
	}

	public String getExternalId2() {
		return externalId2;
	}

	public Employee setExternalId2(String internalId) {
		this.externalId2 = internalId;
		return this;
	}
}
