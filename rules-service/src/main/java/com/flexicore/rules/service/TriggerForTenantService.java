package com.flexicore.rules.service;

import com.flexicore.model.SecurityTenant;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.request.ScenarioTriggerFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.events.BasicUpdated;
import io.micrometer.core.instrument.Tag;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Extension
@Component
public class TriggerForTenantService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(TriggerForTenantService.class);
    public static final String CACHE_NAME = "triggerCache";

    @Autowired
    private ScenarioTriggerService scenarioTriggerService;
    @Autowired
    @Qualifier("triggerCacheManager")
    private CacheManager triggerCacheManager;

    public TriggerForTenantService(@Qualifier("triggerCacheManager") CacheManager keepAliveBounceCacheManager, CacheMetricsRegistrar cacheMetricsRegistrar){
        cacheMetricsRegistrar.bindCacheToRegistry(keepAliveBounceCacheManager.getCache(CACHE_NAME), Tag.of("cache.manager", "keepAliveBounceCacheManager"));

    }

    @Cacheable(cacheNames = CACHE_NAME,key = "#canonicalName",cacheManager = "triggerCacheManager")
    public List<ScenarioTrigger> getTriggers(String canonicalName) {
        return scenarioTriggerService.listAllScenarioTriggers(new ScenarioTriggerFilter().setEventCanonicalNames(Collections.singleton(canonicalName)),null);
    }

    @EventListener
    public void onScenarioTriggerCreated(BasicCreated<ScenarioTrigger> scenarioTriggerBasicCreated){
        ScenarioTrigger scenarioTrigger = scenarioTriggerBasicCreated.getBaseclass();
        evictRelatedEntries(scenarioTrigger);

    }
    @EventListener
    public void onScenarioTriggerUpdated(BasicUpdated<ScenarioTrigger> scenarioTriggerBasicCreated){
        ScenarioTrigger scenarioTrigger = scenarioTriggerBasicCreated.getBaseclass();
        evictRelatedEntries(scenarioTrigger);

    }


    private void evictRelatedEntries(ScenarioTrigger scenarioTrigger) {
        Optional.ofNullable(triggerCacheManager.getCache(TriggerForTenantService.CACHE_NAME)).ifPresent(f->evictRelatedEntries(scenarioTrigger,f));
    }

    private void evictRelatedEntries(ScenarioTrigger scenarioTrigger, Cache cache) {
        if(scenarioTrigger.getScenarioTriggerType()==null){
            return;
        }
        cache.evict(scenarioTrigger.getScenarioTriggerType().getEventCanonicalName());
    }

    public static String getCacheKey(String canonicalName, List<SecurityTenant> tenants) {
        return canonicalName+"_"+tenants.stream().map(f->f.getId()).sorted().collect(Collectors.joining(","));
    }
}
