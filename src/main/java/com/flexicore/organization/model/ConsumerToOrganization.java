package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baselink;
import com.flexicore.security.SecurityContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ConsumerToOrganization extends Baselink {

	public ConsumerToOrganization() {
	}

	public ConsumerToOrganization(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	@Override
	@ManyToOne(targetEntity = Consumer.class, cascade = {CascadeType.MERGE,
			CascadeType.PERSIST})
	public Consumer getLeftside() {
		return (Consumer) super.getLeftside();
	}

	public void setLeftside(Consumer leftside) {
		super.setLeftside(leftside);
	}

	@Override
	public void setLeftside(Baseclass leftside) {
		super.setLeftside(leftside);
	}

	@Override
	@ManyToOne(targetEntity = Organization.class, cascade = {CascadeType.MERGE,
			CascadeType.PERSIST})
	public Organization getRightside() {
		return (Organization) super.getRightside();
	}

	public void setRightside(Organization rightside) {
		super.setRightside(rightside);
	}

	@Override
	public void setRightside(Baseclass rightside) {
		super.setRightside(rightside);
	}

}
