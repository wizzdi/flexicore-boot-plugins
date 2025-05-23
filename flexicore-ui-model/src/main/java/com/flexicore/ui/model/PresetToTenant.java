package com.flexicore.ui.model;

import com.flexicore.model.SecurityTenant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class PresetToTenant extends PresetToEntity {

	@ManyToOne(targetEntity = SecurityTenant.class, cascade = { CascadeType.MERGE,
			CascadeType.PERSIST })
	private SecurityTenant targetTenant;

	public PresetToTenant() {
	}


	@ManyToOne(targetEntity = SecurityTenant.class, cascade = { CascadeType.MERGE,
			CascadeType.PERSIST })
	public SecurityTenant getTargetTenant() {
		return targetTenant;
	}

	public <T extends PresetToTenant> T setTargetTenant(SecurityTenant tenant) {
		this.targetTenant = tenant;
		return (T) this;
	}

	@Override
	@Transient
	public SecurityTenant getEntity() {
		return targetTenant;
	}

}
