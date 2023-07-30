package com.wizzdi.maps.service.service;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.events.BasicUpdated;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.service.request.StatusHistoryCreate;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.OffsetDateTime;

@Component
@Extension
public class StatusHistoryListener implements Plugin {
    @Autowired
    private StatusHistoryCreator statusHistoryCreator;


    @TransactionalEventListener
    public void onMappedPOICreated(BasicCreated<MappedPOI> mappedPoiCreated){
        MappedPOI mappedPOI = mappedPoiCreated.getBaseclass();
        if(mappedPOI.isKeepStatusHistory()){

            statusHistoryCreator.createStatusHistory(mappedPOI);
        }
    }


    @TransactionalEventListener
    public void onMappedPOIUpdated(BasicUpdated<MappedPOI> mappedPOIBasicUpdated){
        MappedPOI mappedPOI = mappedPOIBasicUpdated.getBaseclass();
        if(mappedPOI.isKeepStatusHistory()){
            statusHistoryCreator.createStatusHistory(mappedPOI);
        }
    }
}
