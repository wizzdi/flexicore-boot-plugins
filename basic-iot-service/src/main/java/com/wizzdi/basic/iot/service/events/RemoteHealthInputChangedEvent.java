package com.wizzdi.basic.iot.service.events;

import com.wizzdi.basic.iot.model.HealthSignalSourceType;

import java.time.OffsetDateTime;
import java.util.Set;

/**
 * Describes inputs that may affect the typed health projection of a Remote.
 * The event intentionally carries stable state paths/source types rather than
 * the complete state payload.
 */
public record RemoteHealthInputChangedEvent(
        String remoteId,
        Set<String> changedStatePaths,
        Set<HealthSignalSourceType> changedSourceTypes,
        boolean force,
        OffsetDateTime occurredAt) {

    public RemoteHealthInputChangedEvent {
        changedStatePaths = changedStatePaths == null ? Set.of() : Set.copyOf(changedStatePaths);
        changedSourceTypes = changedSourceTypes == null ? Set.of() : Set.copyOf(changedSourceTypes);
        occurredAt = occurredAt == null ? OffsetDateTime.now() : occurredAt;
    }

    public static RemoteHealthInputChangedEvent connectivity(String remoteId, OffsetDateTime occurredAt) {
        return new RemoteHealthInputChangedEvent(
                remoteId,
                Set.of(),
                Set.of(HealthSignalSourceType.REMOTE_CONNECTIVITY),
                false,
                occurredAt);
    }
}
