package com.wizzdi.basic.iot.service.service;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Component
@Extension
public class SemaphoreMetrics implements Plugin {

    private final Semaphore virtualThreadsLogicSemaphore;

    public SemaphoreMetrics(MeterRegistry meterRegistry, Semaphore virtualThreadsLogicSemaphore) {
        this.virtualThreadsLogicSemaphore = virtualThreadsLogicSemaphore;
        
        Gauge.builder("iot.available", virtualThreadsLogicSemaphore, Semaphore::availablePermits)
            .description("Available permits")
            .register(meterRegistry);
            
        Gauge.builder("iot.queue.length", virtualThreadsLogicSemaphore, s -> s.getQueueLength())
            .description("Number of threads waiting to acquire")
            .register(meterRegistry);
    }
}


