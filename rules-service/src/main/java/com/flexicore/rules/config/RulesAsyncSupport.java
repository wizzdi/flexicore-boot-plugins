package com.flexicore.rules.config;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
@Extension
@EnableAsync(proxyTargetClass = true)
public class RulesAsyncSupport implements Plugin {

    @Bean
    public Executor rulesExecutor() {
        ThreadFactory factory = Thread.ofVirtual()
                .name("rules-thread-").factory();
        return Executors.newThreadPerTaskExecutor(factory);
    }

}