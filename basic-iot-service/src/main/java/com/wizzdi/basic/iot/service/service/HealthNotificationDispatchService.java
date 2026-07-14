package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.model.HealthNotificationChannel;
import com.wizzdi.basic.iot.model.HealthNotificationDelivery;
import com.wizzdi.basic.iot.model.HealthNotificationDeliveryMode;
import com.wizzdi.basic.iot.model.HealthNotificationDeliveryStatus;
import com.wizzdi.basic.iot.model.HealthNotificationOutbox;
import com.wizzdi.basic.iot.service.data.HealthNotificationRepository;
import com.wizzdi.basic.iot.service.notification.HealthNotificationChannelAdapter;
import com.wizzdi.basic.iot.service.notification.HealthNotificationItem;
import com.wizzdi.basic.iot.service.notification.HealthNotificationMessage;
import com.wizzdi.basic.iot.service.notification.HealthNotificationProviderUnavailableException;
import com.wizzdi.basic.iot.service.notification.HealthNotificationSendResult;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Extension
@Component
public class HealthNotificationDispatchService implements Plugin {
    private static final Logger logger = LoggerFactory.getLogger(HealthNotificationDispatchService.class);

    @Autowired
    private HealthNotificationRepository repository;
    @Autowired
    @Qualifier("healthEvaluationExecutor")
    private ExecutorService executor;
    @Autowired(required = false)
    private List<HealthNotificationChannelAdapter> adapters = List.of();
    @Value("${basic.iot.health.notifications.dispatchBatchSize:2000}")
    private int dispatchBatchSize;
    @Value("${basic.iot.health.notifications.maxAttempts:10}")
    private int maxAttempts;
    @Value("${basic.iot.health.notifications.adapterRetrySeconds:300}")
    private long adapterRetrySeconds;
    @Value("${basic.iot.health.notifications.processingTimeoutSeconds:600}")
    private long processingTimeoutSeconds;
    private final AtomicBoolean dispatching = new AtomicBoolean();

    @Scheduled(
            initialDelayString = "${basic.iot.health.notifications.dispatchInitialDelayMs:5000}",
            fixedDelayString = "${basic.iot.health.notifications.dispatchIntervalMs:1000}")
    public void scheduleDispatch() {
        if (!dispatching.compareAndSet(false, true)) return;
        executor.execute(() -> {
            try {
                dispatchDue();
            } catch (RuntimeException e) {
                logger.error("Health notification dispatch failed", e);
            } finally {
                dispatching.set(false);
            }
        });
    }

    public void dispatchDue() {
        OffsetDateTime now = OffsetDateTime.now();
        List<HealthNotificationDelivery> due = repository.claimDue(
                now,
                now.minusSeconds(Math.max(60, processingTimeoutSeconds)),
                Math.max(1, dispatchBatchSize));
        if (due.isEmpty()) return;
        Map<DispatchKey, List<HealthNotificationDelivery>> groups = new LinkedHashMap<>();
        for (HealthNotificationDelivery delivery : due) {
            DispatchKey key = key(delivery);
            groups.computeIfAbsent(key, ignored -> new ArrayList<>()).add(delivery);
        }
        for (List<HealthNotificationDelivery> deliveries : groups.values()) {
            dispatch(deliveries, now);
        }
    }

    private DispatchKey key(HealthNotificationDelivery delivery) {
        if (delivery.getDeliveryMode() == HealthNotificationDeliveryMode.IMMEDIATE) {
            return new DispatchKey(delivery.getId(), delivery.getScheduledAt());
        }
        String preferenceId = delivery.getChannelPreference() == null
                ? delivery.getId() : delivery.getChannelPreference().getId();
        return new DispatchKey(preferenceId, delivery.getScheduledAt());
    }

    private void dispatch(List<HealthNotificationDelivery> deliveries, OffsetDateTime now) {
        if (deliveries == null || deliveries.isEmpty()) return;
        HealthNotificationDelivery first = deliveries.get(0);

        if (first.getChannel() == HealthNotificationChannel.IN_APP) {
            markDelivered(deliveries, now, new HealthNotificationSendResult(null, 200, "Persisted in application"));
            return;
        }
        HealthNotificationMessage message = message(deliveries);
        HealthNotificationChannelAdapter adapter = adapters.stream()
                .filter(candidate -> candidate.channel() == first.getChannel())
                .filter(candidate -> candidate.supports(message))
                .max(Comparator.comparingInt(HealthNotificationChannelAdapter::priority))
                .orElse(null);
        if (adapter == null) {
            markWaiting(deliveries, now, "No configured adapter for " + first.getChannel());
            return;
        }
        try {
            HealthNotificationSendResult result = adapter.send(message);
            markDelivered(deliveries, now, result);
        } catch (HealthNotificationProviderUnavailableException e) {
            markWaiting(deliveries, now, e.getMessage());
        } catch (Exception e) {
            logger.warn("Failed delivering {} health notification(s) through {}",
                    deliveries.size(), first.getChannel(), e);
            deliveries.forEach(delivery -> {
                delivery.setStatus(HealthNotificationDeliveryStatus.FAILED);
                if (delivery.getAttemptCount() >= maxAttempts) {
                    delivery.setNextAttemptAt(null);
                } else {
                    long delay = Math.min(3600,
                            Math.max(30, adapterRetrySeconds) * (1L << Math.min(6, delivery.getAttemptCount() - 1)));
                    delivery.setNextAttemptAt(now.plusSeconds(delay));
                }
                delivery.setLastError(trim(e.getMessage(), 3900));
            });
            repository.massMerge(deliveries);
        }
    }

    private void markWaiting(List<HealthNotificationDelivery> deliveries, OffsetDateTime now, String reason) {
        deliveries.forEach(delivery -> {
            delivery.setStatus(HealthNotificationDeliveryStatus.WAITING_FOR_ADAPTER);
            delivery.setLastError(trim(reason, 3900));
            delivery.setNextAttemptAt(now.plusSeconds(Math.max(30, adapterRetrySeconds)));
        });
        repository.massMerge(deliveries);
    }

    private void markDelivered(List<HealthNotificationDelivery> deliveries,
                               OffsetDateTime now,
                               HealthNotificationSendResult result) {
        deliveries.forEach(delivery -> {
            delivery.setStatus(HealthNotificationDeliveryStatus.DELIVERED);
            delivery.setDeliveredAt(now);
            delivery.setNextAttemptAt(null);
            delivery.setLastError(null);
            if (result != null) {
                delivery.setProviderMessageId(result.providerMessageId());
                delivery.setProviderResponseCode(result.responseCode());
                delivery.setProviderResponse(trim(result.providerResponse(), 1900));
            }
        });
        repository.massMerge(deliveries);
    }

    private HealthNotificationMessage message(List<HealthNotificationDelivery> deliveries) {
        HealthNotificationDelivery first = deliveries.get(0);
        List<HealthNotificationItem> items = deliveries.stream().map(this::item).toList();
        boolean summary = first.getDeliveryMode() != HealthNotificationDeliveryMode.IMMEDIATE;
        String subject = summary
                ? "Health " + first.getDeliveryMode().name().toLowerCase().replace('_', ' ')
                : first.getHealthNotificationOutbox().getTitle();
        String body = summary
                ? items.stream().map(item -> "- " + item.title() + ": " + item.message())
                        .collect(Collectors.joining("\n"))
                : first.getHealthNotificationOutbox().getMessage();
        String locale = first.getChannelPreference() == null
                ? "en" : first.getChannelPreference().getLocale();
        return new HealthNotificationMessage(
                first.getTenant() == null ? null : first.getTenant().getId(),
                first.getUser() == null ? null : first.getUser().getId(),
                first.getDestination(),
                first.getChannel(),
                first.getDeliveryMode(),
                locale == null || locale.isBlank() ? "en" : locale,
                subject,
                body,
                items);
    }

    private HealthNotificationItem item(HealthNotificationDelivery delivery) {
        HealthNotificationOutbox outbox = delivery.getHealthNotificationOutbox();
        return new HealthNotificationItem(
                delivery.getId(),
                outbox.getEventId(),
                outbox.getEventType(),
                outbox.getHealthIncident() == null ? null : outbox.getHealthIncident().getId(),
                outbox.getRemote() == null ? null : outbox.getRemote().getId(),
                outbox.getRemoteGroup() == null ? null : outbox.getRemoteGroup().getId(),
                outbox.getTitle(),
                outbox.getMessage(),
                outbox.getSeverityName(),
                outbox.getSeverityValue(),
                outbox.getOccurredAt());
    }

    private String trim(String value, int max) {
        if (value == null) return null;
        return value.length() <= max ? value : value.substring(0, max);
    }

    private record DispatchKey(String preferenceOrDeliveryId, OffsetDateTime scheduledAt) { }
}
