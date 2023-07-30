package com.flexicore.license.service;


import com.flexicore.license.annotations.HasFeature;
import com.flexicore.license.annotations.HasFeatures;
import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.model.LicensingProduct;
import com.flexicore.license.request.LicensingFeatureCreate;
import com.flexicore.license.request.LicensingFeatureFiltering;
import com.flexicore.license.request.LicensingProductCreate;
import com.flexicore.license.request.LicensingProductFiltering;
import com.wizzdi.flexicore.boot.base.events.PluginsLoadedEvent;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;


import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Extension

@Component
public class LicenseScanner implements Plugin {


    private static final Logger logger= LoggerFactory.getLogger(LicenseScanner.class);
    @Autowired

    private LicensingFeatureService featureService;
    @Autowired

    private LicensingProductService licensingProductService;

    @Autowired
    @Lazy
    private PluginManager pluginManager;

    @Async
    @EventListener
    public void init(PluginsLoadedEvent pluginsLoadedEvent){
        List<HasFeature> features = getFeatureAnnotations();
        Set<String> featuresCanonicalNames = features.stream().map(f -> f.canonicalName()).filter(f->!f.isEmpty()).collect(Collectors.toSet());
        Map<String,LicensingFeature> licensingFeatures = featuresCanonicalNames.isEmpty()?new HashMap<>():featureService.listAllLicensingFeatures(new LicensingFeatureFiltering().setCanonicalNames(featuresCanonicalNames), null).stream().collect(Collectors.toMap(f->f.getId(), f->f));
        Set<String> productsCanonicalNames = features.stream().map(f -> f.productCanonicalName()).filter(f->!f.isEmpty()).collect(Collectors.toSet());
        Map<String,LicensingProduct> licensingProducts = features.isEmpty()?new HashMap<>():licensingProductService.listAllLicensingProducts(new LicensingProductFiltering().setCanonicalNames(productsCanonicalNames), null).stream().collect(Collectors.toMap(f->f.getId(), f->f));
        List<Object> toMerge=new ArrayList<>();
        for (HasFeature feature : features) {
            LicensingProduct licensingProduct = handleProduct(licensingProducts, toMerge, feature);
            handleFeature(licensingFeatures, toMerge, feature, licensingProduct);

        }
        featureService.massMerge(toMerge);


    }

    private List<HasFeature> getFeatureAnnotations() {
        List<Class<? extends Plugin>> classes = pluginManager.getExtensionClasses(Plugin.class);
        List<HasFeature> features=new ArrayList<>();
        for (Class<? extends Plugin> aClass : classes) {
            HasFeatures hasFeatures = aClass.getAnnotation(HasFeatures.class);
            if(hasFeatures!=null){
                features.addAll(Arrays.asList(hasFeatures.features()));
            }
            for (Method declaredMethod : aClass.getDeclaredMethods()) {
                HasFeatures hasFeaturesMethod = declaredMethod.getAnnotation(HasFeatures.class);
                if(hasFeaturesMethod!=null){
                    features.addAll(Arrays.asList(hasFeaturesMethod.features()));
                }
            }
        }
        return features;
    }

    private void handleFeature(Map<String, LicensingFeature> licensingFeatures, List<Object> toMerge, HasFeature feature, LicensingProduct licensingProduct) {
        String featureCanonicalName= feature.canonicalName();

        if(!featureCanonicalName.isEmpty()){
            LicensingFeatureCreate licensingFeatureCreate = new LicensingFeatureCreate()
                    .setLicensingProduct(licensingProduct)
                    .setCanonicalName(featureCanonicalName)
                    .setName(featureCanonicalName);
            LicensingFeature licensingFeature = licensingFeatures.get(featureCanonicalName);
            if(licensingFeature==null){
                licensingFeature=featureService.createLicensingFeatureNoMerge(licensingFeatureCreate,null);
                toMerge.add(licensingFeature);
                licensingFeatures.put(featureCanonicalName,licensingFeature);
                logger.debug("Created Feature: "+featureCanonicalName +" ("+licensingFeature.getId()+")");
            }
            else{
                if(featureService.updateLicensingFeatureNoMerge(licensingFeature,licensingFeatureCreate)){
                    toMerge.add(licensingFeature);
                    logger.debug("Updated Feature: "+featureCanonicalName+" ("+licensingFeature.getId()+")");
                }
                else{
                    logger.trace("Unchanged Feature: "+featureCanonicalName+" ("+licensingFeature.getId()+")");

                }
            }
        }
    }

    private LicensingProduct handleProduct(Map<String, LicensingProduct> licensingProducts, List<Object> toMerge, HasFeature feature) {
        LicensingProduct licensingProduct =null;
        String productCanonicalName= feature.productCanonicalName();
        if(!productCanonicalName.isEmpty()){
            LicensingProductCreate licensingProductCreate=new LicensingProductCreate()
                    .setCanonicalName(productCanonicalName)
                    .setName(productCanonicalName);
            licensingProduct = licensingProducts.get(productCanonicalName);
            if(licensingProduct==null){
                licensingProduct=licensingProductService.createLicensingProductNoMerge(licensingProductCreate,null);
                toMerge.add(licensingProduct);
                licensingProducts.put(productCanonicalName,licensingProduct);
                logger.debug("Created Product "+productCanonicalName+" ("+licensingProduct.getId()+")");
            }
            else{
                if(licensingProductService.updateLicensingProductNoMerge(licensingProduct,licensingProductCreate)){
                    toMerge.add(licensingProduct);
                    logger.debug("Updated Product "+productCanonicalName+" ("+licensingProduct.getId()+")");
                }
                else{
                    logger.trace("Unchanged Product "+productCanonicalName+" ("+licensingProduct.getId()+")");

                }
            }
        }
        return licensingProduct;
    }

}
