package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baselink;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class PresetToPreset extends Baseclass {

	@ManyToOne(targetEntity = Preset.class)
	private Preset parentPreset;
	@ManyToOne(targetEntity = Preset.class)
	private Preset childPreset;
	@Lob
	private String sourcePath;
	@Lob
	private String targetPath;

	@ManyToOne(targetEntity = Preset.class)
	public Preset getParentPreset() {
		return parentPreset;
	}

	public <T extends PresetToPreset> T setParentPreset(Preset parentPreset) {
		this.parentPreset = parentPreset;
		return (T) this;
	}

	@ManyToOne(targetEntity = Preset.class)
	public Preset getChildPreset() {
		return childPreset;
	}

	public <T extends PresetToPreset> T setChildPreset(Preset childPreset) {
		this.childPreset = childPreset;
		return (T) this;
	}

	@Lob
	public String getSourcePath() {
		return sourcePath;
	}

	public <T extends PresetToPreset> T setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
		return (T) this;
	}

	@Lob
	public String getTargetPath() {
		return targetPath;
	}

	public <T extends PresetToPreset> T setTargetPath(String targetPath) {
		this.targetPath = targetPath;
		return (T) this;
	}
}
