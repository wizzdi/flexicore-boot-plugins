package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.client.PublicKeyResponse;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.service.request.GatewayFilter;
import com.wizzdi.basic.iot.service.utils.KeyUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Collections;

@Extension
@Component
public class KeepAliveBounceService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(KeepAliveBounceService.class);
    public static final String CACHE_NAME = "keepAliveBounceCache";

    public KeepAliveBounceService(@Qualifier("keepAliveBounceCacheManager") CacheManager keepAliveBounceCacheManager, CacheMetricsRegistrar cacheMetricsRegistrar){
        cacheMetricsRegistrar.bindCacheToRegistry(keepAliveBounceCacheManager.getCache(CACHE_NAME));

    }

    @Cacheable(cacheNames = CACHE_NAME,key = "#remoteId",cacheManager = "keepAliveBounceCacheManager")
    public Long getLastBounce(String remoteId) {
        return 0L;
    }

    @CachePut(cacheNames = CACHE_NAME,key = "#remoteId",cacheManager = "keepAliveBounceCacheManager")
    public Long setLastBounce(String remoteId,Long time) {
        return time;
    }

}
