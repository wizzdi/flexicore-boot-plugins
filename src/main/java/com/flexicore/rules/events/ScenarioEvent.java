package com.flexicore.rules.events;


import com.flexicore.security.SecurityContextBase;

public interface ScenarioEvent {

    String getId() ;
    SecurityContextBase getSecurityContext();


}
