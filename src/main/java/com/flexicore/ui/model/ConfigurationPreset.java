package com.flexicore.ui.model;

import javax.persistence.Entity;
import javax.persistence.Lob;


@Entity
public class ConfigurationPreset extends Preset {
    static ConfigurationPreset s_Singleton = new ConfigurationPreset();
    public static ConfigurationPreset s() {
        return s_Singleton;
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
