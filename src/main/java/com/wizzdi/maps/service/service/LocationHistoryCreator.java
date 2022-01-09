package com.wizzdi.maps.service.service;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.events.BasicUpdated;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import com.wizzdi.maps.model.LocationHistory;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.request.LocationHistoryCreate;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Extension
public class LocationHistoryCreator implements Plugin {
    @Autowired
    @Lazy
    private SecurityContextProvider securityContextProvider;
    @Autowired
    private LocationHistoryService locationHistoryService;


    @EventListener
    @Async
    public void onMappedPOICreated(BasicCreated<MappedPOI> mappedPoiCreated){
        MappedPOI mappedPOI = mappedPoiCreated.getBaseclass();
        if(mappedPOI.isKeepHistory()){

            createLocationHistory(mappedPOI);
        }
    }

    private LocationHistory createLocationHistory(MappedPOI mappedPOI) {
        SecurityContextBase securityContext = securityContextProvider.getSecurityContext(mappedPOI.getSecurity().getCreator());
        securityContext.setTenantToCreateIn(mappedPOI.getSecurity().getTenant());
        return locationHistoryService.createLocationHistory(new LocationHistoryCreate(mappedPOI),securityContext);

    }

    @EventListener
    @Async
    public void onMappedPOIUpdated(BasicUpdated<MappedPOI> mappedPOIBasicUpdated){
        MappedPOI mappedPOI = mappedPOIBasicUpdated.getBaseclass();
        if(mappedPOI.isKeepHistory()){
            createLocationHistory(mappedPOI);
        }
    }
}
