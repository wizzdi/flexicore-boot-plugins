package com.flexicore.rules.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.micrometer.core.instrument.MeterRegistry;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.boot.actuate.metrics.cache.CaffeineCacheMeterBinderProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Extension
@Configuration
@EnableCaching
public class TriggerConfig implements Plugin {




    @Bean
    @Qualifier("triggerCacheManager")
    public CacheManager triggerCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager
                .setCaffeine(Caffeine.newBuilder()
                        .expireAfterAccess(3, TimeUnit.DAYS)
                        .maximumSize(20000)
                .recordStats());
        return cacheManager;
    }

}
