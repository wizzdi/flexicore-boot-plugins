package com.wizzdi.basic.iot.service.events;

import com.flexicore.model.SecurityTenant;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.maps.model.MapIcon;

public record RemoteStatusChanged(Remote remote, MapIcon newStatus, MapIcon currentStatus) {
}
