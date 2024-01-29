package com.flexicore.ui.model;

import com.flexicore.model.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class PresetToRole extends PresetToEntity {

	@ManyToOne(targetEntity = Role.class, cascade = {CascadeType.MERGE,
			CascadeType.PERSIST})
	private Role role;

	public PresetToRole() {
	}


	@ManyToOne(targetEntity = Role.class, cascade = {CascadeType.MERGE,
			CascadeType.PERSIST})
	public Role getRole() {
		return role;
	}

	public <T extends PresetToRole> T setRole(Role role) {
		this.role = role;
		return (T) this;
	}

	@Transient
	@Override
	public Role getEntity() {
		return role;
	}


}
