package com.wizzdi.maps.service.service;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.events.BasicUpdated;
import com.wizzdi.maps.model.MappedPOI;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Extension
public class StatusHistoryListener implements Plugin {
    @Autowired
    private StatusHistoryCreator statusHistoryCreator;

    @Async
    @EventListener
    public void onMappedPOICreated(BasicCreated<MappedPOI> mappedPoiCreated){
        MappedPOI mappedPOI = mappedPoiCreated.getBaseclass();
        if(mappedPOI.isKeepStatusHistory()){

            statusHistoryCreator.createStatusHistory(mappedPOI);
        }
    }


    @Async
    @EventListener
    public void onMappedPOIUpdated(BasicUpdated<MappedPOI> mappedPOIBasicUpdated){
        MappedPOI mappedPOI = mappedPOIBasicUpdated.getBaseclass();
        if(mappedPOI.isKeepStatusHistory()){
            statusHistoryCreator.createStatusHistory(mappedPOI);
        }
    }
}
