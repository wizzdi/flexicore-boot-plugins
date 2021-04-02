package com.flexicore.ui.component.rest;

import com.flexicore.model.Baseclass;
import com.flexicore.ui.component.model.UIComponent;
import com.wizzdi.flexicore.boot.jpa.service.EntitiesHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class EntitiesSeedProvider {

    @Primary
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public EntitiesHolder getSeeds() {
        return new EntitiesHolder(new HashSet<>(Arrays.asList(Baseclass.class, UIComponent.class)));

    }
}
