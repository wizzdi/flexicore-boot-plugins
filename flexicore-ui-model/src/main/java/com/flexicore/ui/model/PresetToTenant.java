package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Role;
import com.flexicore.model.SecurityTenant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class PresetToTenant extends PresetToEntity {

	public PresetToTenant() {
	}


	@Override
	@ManyToOne(targetEntity = Role.class, cascade = {CascadeType.MERGE,
			CascadeType.PERSIST})
	public SecurityTenant getEntity() {
		return (SecurityTenant) super.getEntity();
	}

	@JsonIgnore
	public <T extends PresetToEntity> T setEntity(SecurityTenant entity) {
		super.setEntity(entity);
		return (T) this;
	}
}
