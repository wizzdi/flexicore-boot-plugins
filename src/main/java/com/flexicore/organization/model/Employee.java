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


	@ManyToOne(targetEntity = Organization.class)
	private Organization organization;
	@ManyToOne(targetEntity = Organization.class)
	public Organization getOrganization() {
		return organization;
	}

	public Employee setOrganization(Organization organization) {
		this.organization = organization;
		return this;
	}



}
