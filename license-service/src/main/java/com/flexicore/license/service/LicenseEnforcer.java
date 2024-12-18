package com.flexicore.license.service;


import com.flexicore.license.data.LicenseEnforcerRepository;
import com.flexicore.license.exceptions.ExceededQuota;
import com.flexicore.license.holders.UpdateLicensingCache;
import com.flexicore.license.model.LicenseRequest;
import com.flexicore.license.model.LicenseRequestToQuantityFeature;
import com.flexicore.license.request.ClazzCountRequest;
import com.flexicore.license.request.LicenseRequestFiltering;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureFiltering;
import com.flexicore.license.request.QuotaLimitation;
import com.flexicore.license.response.ClazzCount;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Clazz;
import com.flexicore.model.SecurityTenant;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Extension
@Component
public class LicenseEnforcer implements Plugin {

    private static AtomicBoolean init = new AtomicBoolean(false);

    private final static Cache<String, Integer> requestedLicense = Caffeine.newBuilder().build();
    private final static Cache<String, LicenseRequest> licensed = Caffeine.newBuilder().build();
    private final static Cache<String, AtomicLong> cachedCount = Caffeine.newBuilder().build();
    private final static Map<String, QuotaLimitation> quotaLimitationCache = new ConcurrentHashMap<>();



    private static final Logger logger = LoggerFactory.getLogger(LicenseEnforcer.class);

    @Autowired
    private LicenseRequestToQuantityFeatureService licenseRequestToQuantityFeatureService;

    @Autowired
    private LicenseRequestService licenseRequestService;
    @Autowired
    private LicenseEnforcerRepository licenseEnforcerRepository;


    @EventListener
    public void onBaseclassCreated(BasicCreated<Baseclass> baseclassCreated) {
        Baseclass b = baseclassCreated.getBaseclass();
        if (b.getTenant() != null) {
            String canonicalName = b.getClass().getCanonicalName();
            SecurityTenant tenant = b.getTenant();
            String tenantId = tenant.getId();
            String key = getKey(tenantId, canonicalName);
            QuotaLimitation quotaLimitation = quotaLimitationCache.get(canonicalName);
            if (quotaLimitation != null) {
                    int basic=quotaLimitation.getQuota();
                    Integer extended = requestedLicense.getIfPresent(key);
                    boolean useExtended = extended != null && extended > basic;
                    int allowed= useExtended ?extended:basic;

                    AtomicLong currentQuantity = cachedCount.get(key, (k) -> new AtomicLong(0));
                    long val = currentQuantity.updateAndGet(operand -> operand > allowed ? operand : operand + 1);
                    if(useExtended){
                        LicenseRequest license = licensed.getIfPresent(key);
                        if (license == null) {
                            throw new ExceededQuota("Quota of " + canonicalName + " for tenant +" + tenant.getName() + "(" + tenantId + ") Exceeded - no license");
                        }
                    }

                    if (val > allowed) {
                        throw new ExceededQuota("Quota of " + canonicalName + " for tenant +" + tenant.getName() + "(" + tenantId + ") Exceeded , max is " + allowed + " actual is " + val);
                    }

            }
        }
    }

    @Async
    @EventListener
    public void updateLicensingCache(QuotaLimitation quotaLimitation){
        quotaLimitationCache.compute(quotaLimitation.getClazz().getCanonicalName(), (s, existing) -> existing==null||existing.getQuota() > quotaLimitation.getQuota()?quotaLimitation:existing);
    }


    @Async
    @EventListener
    public void updateLicensingCache(UpdateLicensingCache updateLicensingCache) {
        List<LicenseRequest> licenseRequests = licenseRequestService.listAllLicenseRequests(new LicenseRequestFiltering(), null);
        List<LicenseRequestToQuantityFeature> licenseRequestToQuantityFeatures = licenseRequests.isEmpty() ? new ArrayList<>() : licenseRequestToQuantityFeatureService.listAllLicenseRequestToQuantityFeatures(new LicenseRequestToQuantityFeatureFiltering().setLicenseRequests(licenseRequests), null);
        Map<String, List<LicenseRequestToQuantityFeature>> links = licenseRequestToQuantityFeatures.parallelStream().filter(f -> f.getLicenseRequest() != null).collect(Collectors.groupingBy(f -> f.getLicenseRequest().getId()));

        Set<Clazz> clazzNames = new HashSet<>();
        for (Map.Entry<String, List<LicenseRequestToQuantityFeature>> stringListEntry : links.entrySet()) {
            Boolean validated = null;
            for (LicenseRequestToQuantityFeature licenseRequestToQuantityFeature : stringListEntry.getValue()) {
                if (validated == null) {
                    validated = licenseRequestService.isLicenseValid(licenseRequestToQuantityFeature.getLicenseRequest());
                }
                clazzNames.add(licenseRequestToQuantityFeature.getLicensingEntity().getClazz());
                String key = getKey(licenseRequestToQuantityFeature);
                requestedLicense.put(key, licenseRequestToQuantityFeature.getQuantityLimit());
                if (validated) {
                    licensed.put(key, licenseRequestToQuantityFeature.getLicenseRequest());
                }
            }
        }
        Map<String, Clazz> clazzMap = clazzNames.parallelStream().filter(f -> f != null).collect(Collectors.toMap(f->f.name(),f->f,(a,b)->a));
        List<ClazzCount> clazzCounts = clazzMap.isEmpty() ? new ArrayList<>() : licenseEnforcerRepository.getClazzCount(new ClazzCountRequest().setGroupByTenant(true).setClazzIds(clazzMap.keySet()));
        for (ClazzCount clazzCount : clazzCounts) {
            cachedCount.put(getKey(clazzCount.getTenantId(), clazzCount.getClazzName()), new AtomicLong(clazzCount.getCount()));
        }
    }

    private String getKey(LicenseRequestToQuantityFeature licenseRequestToQuantityFeature) {
        return getKey(licenseRequestToQuantityFeature.getLicenseRequest().getLicensedTenant().getId(), licenseRequestToQuantityFeature.getLicensingEntity().getCanonicalName());
    }

    private String getKey(String tenantId, String canonicalName) {
        return tenantId + ":" + canonicalName;
    }

}
