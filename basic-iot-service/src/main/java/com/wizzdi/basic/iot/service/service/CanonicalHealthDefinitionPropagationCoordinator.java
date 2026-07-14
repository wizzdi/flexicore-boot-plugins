package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.service.data.HealthDefinitionDependencyRepository;
import com.wizzdi.basic.iot.service.events.HealthSignalDefinitionChangedEvent;
import com.wizzdi.basic.iot.service.events.StatePropertyDefinitionChangedEvent;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

@Extension
@Component
public class CanonicalHealthDefinitionPropagationCoordinator implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger("basic-iot");

    @Autowired
    private HealthDefinitionDependencyRepository dependencyRepository;
    @Autowired
    private RemoteHealthProfileService remoteHealthProfileService;
    @Autowired
    @Qualifier("healthEvaluationExecutor")
    private ExecutorService healthEvaluationExecutor;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onHealthSignalDefinitionChanged(HealthSignalDefinitionChangedEvent event) {
        if (event == null || event.healthSignalDefinitionId() == null) {
            return;
        }
        submit("HealthSignalDefinition " + event.healthSignalDefinitionId(), () ->
                invalidateProfiles(dependencyRepository.listProfileIdsByHealthSignalDefinition(
                        event.healthSignalDefinitionId())));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onStatePropertyDefinitionChanged(StatePropertyDefinitionChangedEvent event) {
        if (event == null || event.statePropertyDefinitionId() == null) {
            return;
        }
        submit("StatePropertyDefinition " + event.statePropertyDefinitionId(), () ->
                invalidateProfiles(dependencyRepository.listProfileIdsByStatePropertyDefinition(
                        event.statePropertyDefinitionId())));
    }

    private void invalidateProfiles(Set<String> profileIds) {
        if (profileIds == null || profileIds.isEmpty()) {
            return;
        }
        for (String profileId : profileIds) {
            remoteHealthProfileService.incrementDefinitionVersion(profileId);
        }
        logger.info("invalidated {} RemoteHealthProfile definitions", profileIds.size());
    }

    private void submit(String source, Runnable runnable) {
        try {
            healthEvaluationExecutor.execute(() -> {
                try {
                    runnable.run();
                } catch (Throwable e) {
                    logger.error("failed propagating canonical health definition change for {}", source, e);
                }
            });
        } catch (RejectedExecutionException e) {
            logger.debug("health evaluation executor is shutting down; skipped canonical propagation for {}", source);
        }
    }
}
