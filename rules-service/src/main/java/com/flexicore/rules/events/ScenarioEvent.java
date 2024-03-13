package com.flexicore.rules.events;


import com.flexicore.model.SecurityTenant;
import com.flexicore.security.SecurityContextBase;

import java.util.List;

public interface ScenarioEvent {

    String getId() ;
    List<SecurityTenant> getTenants();


}
