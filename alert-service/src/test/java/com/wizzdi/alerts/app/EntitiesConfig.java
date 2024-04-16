package com.wizzdi.alerts.app;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.security.SecurityPolicy;
import com.wizzdi.alerts.Alert;
import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.flexicore.boot.jpa.service.EntitiesHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class EntitiesConfig {

	@Bean
	public EntitiesHolder entitiesHolder(){
		return new EntitiesHolder(new HashSet<>(Arrays.asList(SecurityTenant.class,Baseclass.class, Basic.class, SecurityPolicy.class, Alert.class, JsonConverter.class)));
	}
}
