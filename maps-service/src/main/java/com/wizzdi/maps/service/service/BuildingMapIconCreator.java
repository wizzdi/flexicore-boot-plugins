package com.wizzdi.maps.service.service;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.maps.model.Building;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.service.request.MapIconCreate;
import com.wizzdi.maps.service.request.MapIconFilter;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Extension
@Configuration
public class BuildingMapIconCreator implements Plugin {


    @Bean
    @Qualifier("building_icon")
    @Order(Ordered.LOWEST_PRECEDENCE)
    public MapIcon buildingMapIcon(@Lazy MapIconService mapIconService,@Lazy SecurityContextBase adminSecurityContext ) {

        String externalId = Building.class.getCanonicalName();
        MapIconCreate mapIconCreate=new MapIconCreate().setRelatedType(Building.class.getCanonicalName()).setExternalId(externalId).setName(Building.class.getSimpleName());
        MapIcon mapIcon = mapIconService.listAllMapIcons(new MapIconFilter().setExternalId(Collections.singleton(externalId)), adminSecurityContext).stream()
                .findFirst().orElseGet(() -> mapIconService.createMapIcon(mapIconCreate, adminSecurityContext));
        if(mapIconService.updateMapIconNoMerge(mapIcon,mapIconCreate)){
            mapIconService.merge(mapIcon);
        }
        return mapIcon;
    }
}
