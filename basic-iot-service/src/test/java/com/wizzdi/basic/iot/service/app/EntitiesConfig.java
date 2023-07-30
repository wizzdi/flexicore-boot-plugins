package com.wizzdi.basic.iot.service.app;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.Clazz;
import com.flexicore.model.Tenant;
import com.flexicore.model.security.SecurityPolicy;

import com.flexicore.model.territories.Address;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.flexicore.boot.jpa.service.EntitiesHolder;

import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.maps.model.MappedPOI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class EntitiesConfig {

	@Bean
	public EntitiesHolder entitiesHolder(){
		return new EntitiesHolder(new HashSet<>(Arrays.asList(Tenant.class, FileResource.class,Baseclass.class, Basic.class, SecurityPolicy.class, Gateway.class, JsonConverter.class, MappedPOI.class, Address.class)));
	}
}
