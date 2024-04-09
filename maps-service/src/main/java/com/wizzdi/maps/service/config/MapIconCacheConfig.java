package com.wizzdi.maps.service.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Extension
@Configuration
@EnableCaching
public class MapIconCacheConfig implements Plugin {




    @Bean
    @Qualifier("mapIconCacheManager")
    public CacheManager mapIconCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager
                .setCaffeine(Caffeine.newBuilder()
                        .expireAfterAccess(3, TimeUnit.DAYS)
                        .maximumSize(20000)
                .recordStats());
        return cacheManager;
    }

}
