package com.wizzdi.basic.iot.service.config;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.service.request.MapIconCreate;
import com.wizzdi.maps.service.request.MapIconFilter;
import com.wizzdi.maps.service.service.MapIconService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@Extension
public class GatewayMapIconConfig implements Plugin {

    public static final String GATEWAY_EXTERNAL_ID = "gateway";

    @Bean
    @Qualifier("gatewayMapIcon")
    public MapIcon gatewayMapIcon(SecurityContext adminSecurityContext, MapIconService mapIconService){
        return mapIconService.listAllMapIcons(new MapIconFilter().setRelatedType(Collections.singleton(Gateway.class.getCanonicalName())),adminSecurityContext).stream().findFirst().orElseGet(()->mapIconService.createMapIcon(new MapIconCreate().setRelatedType(Gateway.class.getCanonicalName()).setExternalId(GATEWAY_EXTERNAL_ID).setName(GATEWAY_EXTERNAL_ID),adminSecurityContext));
    }
}
