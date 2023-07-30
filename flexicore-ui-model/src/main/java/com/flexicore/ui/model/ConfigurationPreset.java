package com.flexicore.ui.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;

@Entity
public class ConfigurationPreset extends Preset {

	public ConfigurationPreset() {
	}

	@Lob
	private String configurationUI;

	@Lob
	public String getConfigurationUI() {
		return configurationUI;
	}

	public ConfigurationPreset setConfigurationUI(String configurationUI) {
		this.configurationUI = configurationUI;
		return this;
	}
}
