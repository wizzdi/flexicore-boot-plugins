package com.flexicore.ui.config;

import com.flexicore.model.Clazz;
import com.flexicore.ui.model.GridPreset;
import com.flexicore.ui.model.Preset;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.Clazzes;
import com.wizzdi.flexicore.security.service.ClazzService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Extension
public class ClazzProvider implements Plugin {


    @Qualifier("gridPresetClazz")
    @Bean
    @Lazy
    public Clazz gridPresetClazz(ClazzService clazzService){
        return clazzService.getClazz(GridPreset.class).orElseThrow(()->new RuntimeException("cannot find clazz gird preset"));
    }


    @Qualifier("presetClazz")
    @Bean
    @Lazy
    public Clazz presetClazz(ClazzService clazzService){
        return clazzService.getClazz(Preset.class).orElseThrow(()->new RuntimeException("cannot find clazz preset"));
    }
}
