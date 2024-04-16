package com.wizzdi.alerts.ws.messages;

import com.wizzdi.alerts.Alert;
import com.wizzdi.alerts.AlertLevel;

import java.time.OffsetDateTime;

public record AlertMessage(String tenantId, String id, AlertLevel alertLevel, String alertContent, String relatedId, String relatedType, String alertCategory,
                           OffsetDateTime handledAt,OffsetDateTime creationDate) {
    public AlertMessage(Alert alert) {
        this(alert.getSecurity().getTenant().getId(),alert.getId(), alert.getAlertLevel(), alert.getAlertContent(), alert.getRelatedId(), alert.getRelatedType(), alert.getAlertCategory(), alert.getHandledAt(),alert.getCreationDate());
    }

    public AlertMessage {
    }
}
