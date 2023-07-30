/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.license.interceptor;

import com.flexicore.license.annotations.HasFeature;
import com.flexicore.license.annotations.HasFeatures;
import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.request.LicensingFeatureFiltering;
import com.flexicore.license.service.LicenseRequestService;
import com.flexicore.license.service.LicensingFeatureService;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.SecurityUser;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.rest.interfaces.AspectPlugin;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import org.springframework.web.server.ResponseStatusException;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Component
@Extension
@Order(100)
public class LicenseImposer implements AspectPlugin {

    private static final Logger logger = LoggerFactory.getLogger(LicenseImposer.class);
    @Autowired
    private LicenseRequestService licenseRequestService;
    @Autowired
    private LicensingFeatureService licensingFeatureService;
    @Around("execution(@com.flexicore.license.annotations.HasFeatures * *(..)) || within(@(@com.flexicore.license.annotations.HasFeatures *) *)|| within(@com.flexicore.license.annotations.HasFeatures *)")
    public Object transformReturn(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Session websocketSession;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] parameters = joinPoint.getArgs();
        String methodName = method.getName();
        logger.info("Method is: " + methodName + " , on Thread " + Thread.currentThread().getName());
        websocketSession = getWebsocketSession(parameters);
        SecurityContextBase securityContext;
        if (websocketSession != null) {
            securityContext = (SecurityContextBase) websocketSession.getUserProperties().get("securityContext");
        } else {
            securityContext = (SecurityContextBase) parameters[parameters.length - 1];

        }

        SecurityUser user = securityContext.getUser();
        List<SecurityTenant> tenants = securityContext.getTenants();

        List<HasFeature> features = new ArrayList<>();
        HasFeatures featuresOnClass = method.getDeclaringClass().getAnnotation(HasFeatures.class);
        if (featuresOnClass != null) {
            features.addAll(Arrays.asList(featuresOnClass.features()));
        }
        HasFeatures featuresOnMethod = method.getAnnotation(HasFeatures.class);
        if(featuresOnMethod!=null){
            if (featuresOnMethod.noOtherLicenseRequired()) {
                features.clear();
            }
            features.addAll(Arrays.asList(featuresOnMethod.features()));
        }
        if ( user != null && tenants != null) {
            Set<String> canonicalNames = features.parallelStream().map(HasFeature::canonicalName).collect(Collectors.toSet());
            List<LicensingFeature> licensingFeatures = canonicalNames.isEmpty() ? new ArrayList<>() : licensingFeatureService.listAllLicensingFeatures(new LicensingFeatureFiltering().setCanonicalNames(canonicalNames), null);

            for (LicensingFeature feature : licensingFeatures) {
                if (!licenseRequestService.isFeatureLicensed(user, tenants, feature)) {
                    return denyLicense(feature, websocketSession);
                }
            }


        }


        return joinPoint.proceed(parameters);


    }

    private Session getWebsocketSession(Object[] parameters) {
        return parameters != null ? Stream.of(parameters).filter(f -> f instanceof Session).map(f -> (Session) f).findAny().orElse(null) : null;
    }


    private void closeWSIfNecessary(Session websocketSession, String reason) {
        if (websocketSession != null && websocketSession.isOpen()) {
            try {
                String id = websocketSession.getId();

                websocketSession.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, reason));
                logger.warn("Closed WS " + id + " for being unauthorized");
            } catch (Exception e) {
                logger.error("failed closing WS", e);
            }
        }
    }

    private Object denyLicense(LicensingFeature feature, Session websocketSession) {
        String s = "invalid Feature License for Feature: " + feature.getCanonicalName();
        closeWSIfNecessary(websocketSession, s);

        throw new ResponseStatusException(HttpStatus.FORBIDDEN,s);
    }
}
