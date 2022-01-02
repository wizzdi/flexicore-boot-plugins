package com.flexicore.scheduling.config;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Extension
public class FCSchedulingConfig implements Plugin {
    @Value("${flexicore.scheduling.max.threads:5}")
    private int maxSchedulingThreads;

    @Bean
    @Qualifier("FCSchedulingExecutor")
    public ExecutorService FCSchedulingExecutor(){
       return Executors
                .newFixedThreadPool(maxSchedulingThreads);
    }
}
