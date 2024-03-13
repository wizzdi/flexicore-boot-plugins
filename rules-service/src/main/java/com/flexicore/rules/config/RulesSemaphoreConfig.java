package com.flexicore.rules.config;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.micrometer.core.instrument.MeterRegistry;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Semaphore;

@Extension
@Configuration
public class RulesSemaphoreConfig implements Plugin {

    @Value("${rules.jdbcRatio:0.2}")
    private float mqttJdbcRatio;
    @Autowired
    private MeterRegistry meterRegistry;



    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maximumPoolSize;

    @Bean
    @Qualifier("rulesLogicSemaphore")
    public Semaphore rulesLogicSemaphore(){
        return new Semaphore((int) (maximumPoolSize*mqttJdbcRatio));
    }
}
