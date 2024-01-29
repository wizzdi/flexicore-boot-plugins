package com.wizzdi.basic.iot.service.config;

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

import java.util.List;
import java.util.concurrent.TimeUnit;

@Extension
@Configuration
@EnableCaching
public class CacheConfig implements Plugin {

    @Bean
    public CacheMetricsRegistrar cacheMetricsRegistrar(MeterRegistry meterRegistry) {
        return new CacheMetricsRegistrar(meterRegistry, List.of(new CaffeineCacheMeterBinderProvider()));
    }


    @Bean
    @Qualifier("publicKeyCacheManager")
    public CacheManager publicKeyCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS)
                .maximumSize(10000)
                .recordStats());
        return cacheManager;
    }

    @Bean
    @Qualifier("keepAliveBounceCacheManager")
    public CacheManager keepAliveBounceCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .recordStats());
        return cacheManager;
    }

}
