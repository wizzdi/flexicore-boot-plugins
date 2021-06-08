package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class PresetToEntity extends SecuredBasic {

	@ManyToOne(targetEntity = Preset.class)
	private Preset preset;
	@ManyToOne(targetEntity = Baseclass.class)
	private Baseclass entity;
	private int priority;
	private boolean enabled;

	public PresetToEntity() {
	}




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

	@ManyToOne(targetEntity = Preset.class)
	public Preset getPreset() {
		return preset;
	}

	public <T extends PresetToEntity> T setPreset(Preset preset) {
		this.preset = preset;
		return (T) this;
	}

	@ManyToOne(targetEntity = Baseclass.class)
	public Baseclass getEntity() {
		return entity;
	}

	public <T extends PresetToEntity> T setEntity(Baseclass entity) {
		this.entity = entity;
		return (T) this;
	}
}
