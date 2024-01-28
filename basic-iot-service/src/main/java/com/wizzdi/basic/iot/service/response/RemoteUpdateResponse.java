package com.wizzdi.basic.iot.service.response;

import com.wizzdi.basic.iot.service.events.RemoteUpdatedEvent;

public record RemoteUpdateResponse(boolean updated, RemoteUpdatedEvent remoteUpdatedEvent) {


}
