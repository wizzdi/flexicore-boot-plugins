package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass;

import com.flexicore.model.SecurityEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class PresetToEntity extends Baseclass {

	@ManyToOne(targetEntity = Preset.class)
	private Preset preset;
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
	@Transient
	public  SecurityEntity getEntity(){
		return null;
	}

}
