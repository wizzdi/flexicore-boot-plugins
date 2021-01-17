package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

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
	private String parentPath;
	@Lob
	private String childPath;

	public PresetToPreset() {
	}

	public PresetToPreset(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

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
	public String getParentPath() {
		return parentPath;
	}

	public <T extends PresetToPreset> T setParentPath(String sourcePath) {
		this.parentPath = sourcePath;
		return (T) this;
	}

	@Lob
	public String getChildPath() {
		return childPath;
	}

	public <T extends PresetToPreset> T setChildPath(String targetPath) {
		this.childPath = targetPath;
		return (T) this;
	}
}
