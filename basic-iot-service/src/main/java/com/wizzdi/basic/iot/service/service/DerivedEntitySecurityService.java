package com.wizzdi.basic.iot.service.service;

import com.flexicore.model.Baseclass;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Applies tenant/security ownership to records derived from another secured entity.
 *
 * API-created child records keep the authenticated creator/security object and only
 * inherit the authoritative tenant. System-created projections inherit the complete
 * security ownership because there is no request SecurityContext involved.
 */
@Extension
@Component
public class DerivedEntitySecurityService implements Plugin {

    @Autowired
    private SecurityContextProvider securityContextProvider;

    public SecurityContext creationContext(SecurityContext requestContext, Baseclass source) {
        if (requestContext == null || source == null || source.getTenant() == null) {
            return requestContext;
        }
        SecurityContext creationContext = securityContextProvider.getSecurityContext(requestContext.getUser());
        if (creationContext == null) {
            return requestContext;
        }
        creationContext.setTenantToCreateIn(source.getTenant());
        return creationContext;
    }

    public <T extends Baseclass> T setTenantFromRemote(T target, Remote remote) {
        return setTenantFrom(target, remote);
    }

    public <T extends Baseclass> T setTenantFromGroup(T target, RemoteGroup group) {
        return setTenantFrom(target, group);
    }

    public <T extends Baseclass> T setTenantFrom(T target, Baseclass source) {
        if (target != null && source != null) {
            target.setTenant(source.getTenant());
        }
        return target;
    }

    public <T extends Baseclass> T inheritFromRemote(T target, Remote remote) {
        return inherit(target, remote);
    }

    public <T extends Baseclass> T inheritFromGroup(T target, RemoteGroup group) {
        return inherit(target, group);
    }

    public <T extends Baseclass> T inherit(T target, Baseclass source) {
        if (target == null || source == null) {
            return target;
        }
        target.setTenant(source.getTenant());
        target.setCreator(source.getCreator());
        target.setSecurityId(source.getSecurityId());
        return target;
    }
}
