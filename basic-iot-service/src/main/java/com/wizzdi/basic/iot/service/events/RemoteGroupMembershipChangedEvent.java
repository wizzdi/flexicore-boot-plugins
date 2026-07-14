package com.wizzdi.basic.iot.service.events;

import java.time.OffsetDateTime;
import java.util.Set;

public record RemoteGroupMembershipChangedEvent(
        Set<String> remoteGroupIds,
        Set<String> remoteIds,
        OffsetDateTime occurredAt) {
}
