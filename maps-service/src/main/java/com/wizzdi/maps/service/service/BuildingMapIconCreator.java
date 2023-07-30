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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Extension
@Component
public class BuildingMapIconCreator implements Plugin {
    @Autowired
    private MapIconService mapIconService;

    @Autowired
    @Lazy
    private SecurityContextBase adminSecurityContext;

    @Qualifier("building_icon")
    @Bean
    public MapIcon buildingMapIcon() {

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
