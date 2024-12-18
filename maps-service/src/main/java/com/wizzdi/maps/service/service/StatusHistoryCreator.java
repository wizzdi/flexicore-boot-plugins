package com.wizzdi.maps.service.service;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.events.BasicUpdated;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.service.request.LocationHistoryCreate;
import com.wizzdi.maps.service.request.StatusHistoryCreate;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.OffsetDateTime;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
@Extension
public class StatusHistoryCreator implements Plugin {
    @Autowired
    @Lazy
    private SecurityContextProvider securityContextProvider;
    @Autowired
    private StatusHistoryService statusHistoryService;




    @Transactional(propagation = REQUIRES_NEW)
    public StatusHistory createStatusHistory(MappedPOI mappedPOI) {
        SecurityContext securityContext = securityContextProvider.getSecurityContext(mappedPOI.getCreator());
        securityContext.setTenantToCreateIn(mappedPOI.getTenant());
        OffsetDateTime now = OffsetDateTime.now();
        String name = mappedPOI.getName() + " status change to " + (mappedPOI.getMapIcon()!=null?mappedPOI.getMapIcon().getName():"null") + " at " + now;
        StatusHistoryCreate statusHistoryCreate = new StatusHistoryCreate()
                .setMappedPOI(mappedPOI)
                .setMapIcon(mappedPOI.getMapIcon())
                .setDateAtStatus(now)
                .setName(name) ;
        return statusHistoryService.createStatusHistory(statusHistoryCreate,securityContext);

    }

}
