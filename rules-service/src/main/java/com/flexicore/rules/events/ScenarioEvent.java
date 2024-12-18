package com.flexicore.rules.events;


import com.flexicore.model.SecurityTenant;
import com.wizzdi.flexicore.security.configuration.SecurityContext;

import java.util.List;

public interface ScenarioEvent {

    String getId() ;
    List<SecurityTenant> getTenants();


}
