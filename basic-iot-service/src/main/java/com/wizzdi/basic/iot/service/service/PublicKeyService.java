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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Collections;

@Extension
@Component
public class PublicKeyService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(PublicKeyService.class);
    public static final String CACHE_NAME = "publicKeyCache";
    private final GatewayService gatewayService;

    public PublicKeyService(GatewayService gatewayService, @Qualifier("publicKeyCacheManager") CacheManager publicKeyCacheManager, CacheMetricsRegistrar cacheMetricsRegistrar) {
        this.gatewayService = gatewayService;
        cacheMetricsRegistrar.bindCacheToRegistry(publicKeyCacheManager.getCache(CACHE_NAME));

    }

    @Cacheable(cacheNames = CACHE_NAME,key = "#remoteId",cacheManager = "publicKeyCacheManager")
    public PublicKeyResponse getPublicKeyForGateway(String remoteId) {
        return gatewayService.listAllGateways(null, new GatewayFilter().setRemoteIds(Collections.singleton(remoteId))).stream().findFirst()
                .map(e -> getPublicKeyResponse(e)).orElse(null);
    }

    private PublicKeyResponse getPublicKeyResponse(Gateway e) {
        if(e.isNoSignatureCapabilities()){
            return new PublicKeyResponse(null,false);
        }
        return new PublicKeyResponse(readPublicKey(e.getPublicKey()),true);
    }

    public static PublicKey readPublicKey(String publicKeyUnwrapped) {

        try {
            return KeyUtils.readPublicKeyUnwrapped(publicKeyUnwrapped);
        } catch (Exception e) {
            logger.error("failed parsing public key", e);
            return null;
        }
    }
}