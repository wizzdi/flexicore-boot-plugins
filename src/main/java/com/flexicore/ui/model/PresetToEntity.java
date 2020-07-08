package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baselink;
import com.flexicore.security.SecurityContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class PresetToEntity extends Baselink {

	public PresetToEntity() {
	}

	public PresetToEntity(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	private int priority;
	private boolean enabled;

	public int getPriority() {
		return priority;
	}

	public <T extends PresetToEntity> T setPriority(int priority) {
		this.priority = priority;
		return (T) this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public <T extends PresetToEntity> T setEnabled(boolean enabled) {
		this.enabled = enabled;
		return (T) this;
	}

	@Override
	@ManyToOne(targetEntity = Preset.class, cascade = {CascadeType.MERGE,
			CascadeType.PERSIST})
	public Preset getLeftside() {
		return (Preset) super.getLeftside();
	}

	public void setLeftside(Preset leftside) {
		super.setLeftside(leftside);
	}

	@Override
	public void setLeftside(Baseclass leftside) {
		super.setLeftside(leftside);
	}

}
