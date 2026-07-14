package com.flexicore.rules.config;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
@Extension
@EnableAsync(proxyTargetClass = true)
public class RulesAsyncSupport implements Plugin {

    @Bean(destroyMethod = "close")
    public ExecutorService rulesExecutor() {
        ThreadFactory factory = Thread.ofVirtual()
                .name("rules-thread-", 0)
                .factory();
        return Executors.newThreadPerTaskExecutor(factory);
    }

}
