package com.flexicore.ui.config;

import com.flexicore.ui.model.GridPreset;
import com.flexicore.ui.model.Preset;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.jpa.service.EntitiesHolder;
import org.pf4j.Extension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@Extension
public class FlexicoreUIEntitiesProviderConfig implements Plugin {
    @Bean
    public EntitiesHolder flexicoreUIEntitiesHolder(){
        return new EntitiesHolder(Set.of(Preset.class, GridPreset.class));
    }
}
