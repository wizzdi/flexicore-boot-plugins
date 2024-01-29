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
	private SecurityTenant tenant;

	public PresetToTenant() {
	}


	@ManyToOne(targetEntity = SecurityTenant.class, cascade = { CascadeType.MERGE,
			CascadeType.PERSIST })
	public SecurityTenant getTenant() {
		return tenant;
	}

	public <T extends PresetToTenant> T setTenant(SecurityTenant tenant) {
		this.tenant = tenant;
		return (T) this;
	}

	@Override
	@Transient
	public SecurityTenant getEntity() {
		return tenant;
	}

}
