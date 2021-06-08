package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Role;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class PresetToRole extends PresetToEntity {

	public PresetToRole() {
	}



	@Override
	@ManyToOne(targetEntity = Role.class, cascade = {CascadeType.MERGE,
			CascadeType.PERSIST})
	public Role getEntity() {
		return (Role) super.getEntity();
	}

	@JsonIgnore
	public <T extends PresetToEntity> T setEntity(Role entity) {
		super.setEntity(entity);
		return (T) this;
	}

}
