package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Role;
import com.flexicore.model.SecurityUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class PresetToUser extends PresetToEntity {

	@ManyToOne(targetEntity = SecurityUser.class, cascade = { CascadeType.MERGE,
			CascadeType.PERSIST })
	private SecurityUser user;

	public PresetToUser() {
	}

	@ManyToOne(targetEntity = SecurityUser.class, cascade = { CascadeType.MERGE,
			CascadeType.PERSIST })
	public SecurityUser getUser() {
		return user;
	}

	public <T extends PresetToUser> T setUser(SecurityUser user) {
		this.user = user;
		return (T) this;
	}

	@Override
	@Transient
	public SecurityUser getEntity() {
		return user;
	}


}
