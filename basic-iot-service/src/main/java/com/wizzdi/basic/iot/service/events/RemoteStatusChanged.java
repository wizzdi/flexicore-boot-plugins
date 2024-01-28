package com.wizzdi.basic.iot.service.events;

import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.maps.model.MapIcon;

public record RemoteStatusChanged(Remote remote, MapIcon newStatus, MapIcon currentStatus) {
}
