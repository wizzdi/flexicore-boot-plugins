package com.wizzdi.basic.iot.service.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.wizzdi.basic.iot.service.service.KeepAliveBounceService;
import com.wizzdi.basic.iot.service.service.PublicKeyService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.boot.actuate.metrics.cache.CaffeineCacheMeterBinderProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.Cache;
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
public class CacheConfig implements Plugin {


    @Bean
    @Primary
    public CacheManager defaultCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterAccess(3, TimeUnit.HOURS)
                .maximumSize(10000)
                .recordStats());
        return cacheManager;
    }

    @Bean
    @Qualifier("publicKeyCacheManager")
    public CacheManager publicKeyCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterAccess(3, TimeUnit.DAYS)
                .maximumSize(20000)
                .recordStats());
        return cacheManager;
    }

    @Bean
    @Qualifier("keepAliveBounceCacheManager")
    public CacheManager keepAliveBounceCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager
                .setCaffeine(Caffeine.newBuilder()
                        .expireAfterAccess(3, TimeUnit.DAYS)
                        .maximumSize(20000)
                .recordStats());

        return cacheManager;
    }

    @Bean
    @Qualifier(KeepAliveBounceService.CACHE_NAME)
    public Cache keepAliveBounceCache(@Qualifier("keepAliveBounceCacheManager")CacheManager keepAliveBounceCacheManager,CacheMetricsRegistrar cacheMetricsRegistrar) {
        Cache cache = keepAliveBounceCacheManager.getCache(KeepAliveBounceService.CACHE_NAME);
        cacheMetricsRegistrar.bindCacheToRegistry(cache, Tag.of("cache.manager", "keepAliveBounceCacheManager"));
        return cache;
    }

    @Bean
    @Qualifier(PublicKeyService.CACHE_NAME)
    public Cache publicKeyCache(@Qualifier("publicKeyCacheManager")CacheManager publicKeyCacheManager,CacheMetricsRegistrar cacheMetricsRegistrar) {
        Cache cache = publicKeyCacheManager.getCache(PublicKeyService.CACHE_NAME);
        cacheMetricsRegistrar.bindCacheToRegistry(cache, Tag.of("cache.manager", "publicKeyCacheManager"));
        return cache;
    }

}
