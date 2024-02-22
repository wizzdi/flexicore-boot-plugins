package com.flexicore.rules.service;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Component
@Extension
public class RulesSemaphoreMetrics implements Plugin {


    public RulesSemaphoreMetrics(MeterRegistry meterRegistry, @Qualifier("rulesLogicSemaphore") Semaphore rulesLogicSemaphore) {

        Gauge.builder("rules.available", rulesLogicSemaphore, Semaphore::availablePermits)
            .description("Available permits for rule engine")
            .register(meterRegistry);
            
        Gauge.builder("rules.queue.length", rulesLogicSemaphore, s -> s.getQueueLength())
            .description("Number of threads waiting to acquire a rule engine permit")
            .register(meterRegistry);
    }
}


