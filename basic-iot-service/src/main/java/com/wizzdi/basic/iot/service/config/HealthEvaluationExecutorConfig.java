package com.wizzdi.basic.iot.service.config;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;

@Extension
@Configuration
public class HealthEvaluationExecutorConfig implements Plugin {

    @Bean(name = "healthEvaluationExecutor", destroyMethod = "close")
    public ExecutorService healthEvaluationExecutor() {
        ThreadFactory factory = Thread.ofVirtual()
                .name("iot-health-", 0)
                .factory();
        return Executors.newThreadPerTaskExecutor(factory);
    }

    @Bean("healthRemoteEvaluationPermits")
    public Semaphore healthRemoteEvaluationPermits(
            @Value("${basic.iot.health.maxConcurrentRemoteEvaluations:1024}") int maximumConcurrency) {
        return new Semaphore(Math.max(1, maximumConcurrency));
    }

    @Bean("healthGroupEvaluationPermits")
    public Semaphore healthGroupEvaluationPermits(
            @Value("${basic.iot.health.maxConcurrentGroupEvaluations:256}") int maximumConcurrency) {
        return new Semaphore(Math.max(1, maximumConcurrency));
    }
}
