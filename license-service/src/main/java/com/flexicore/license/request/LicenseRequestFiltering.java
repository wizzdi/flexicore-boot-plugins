package com.flexicore.license.request;


import com.fasterxml.jackson.annotation.JsonIgnore;

import com.flexicore.model.SecurityTenant;
import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.model.LicensingProduct;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Asaf on 17/10/2016.
 */
public class LicenseRequestFiltering extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;

    @JsonIgnore
    private List<LicensingFeature> licensingFeatures;

    @JsonIgnore
    private List<LicensingProduct> licensingProducts;

    @JsonIgnore
    private List<SecurityTenant> relatedTenant;
    private Set<String> relatedTenantsIds=new HashSet<>();

    private Boolean signed;
    private OffsetDateTime expirationDateAfter;


    @JsonIgnore
    public List<LicensingFeature> getLicensingFeatures() {
        return licensingFeatures;
    }

    public <T extends LicenseRequestFiltering> T setLicensingFeatures(List<LicensingFeature> licensingFeatures) {
        this.licensingFeatures = licensingFeatures;
        return (T) this;
    }

    public Boolean getSigned() {
        return signed;
    }

    public <T extends LicenseRequestFiltering> T setSigned(Boolean signed) {
        this.signed = signed;
        return (T) this;
    }

    @JsonIgnore
    public List<LicensingProduct> getLicensingProducts() {
        return licensingProducts;
    }

    public <T extends LicenseRequestFiltering> T setLicensingProducts(List<LicensingProduct> licensingProducts) {
        this.licensingProducts = licensingProducts;
        return (T) this;
    }

    public OffsetDateTime getExpirationDateAfter() {
        return expirationDateAfter;
    }

    public <T extends LicenseRequestFiltering> T setExpirationDateAfter(OffsetDateTime expirationDateAfter) {
        this.expirationDateAfter = expirationDateAfter;
        return (T) this;
    }

    @JsonIgnore
    public List<SecurityTenant> getRelatedTenant() {
        return relatedTenant;
    }

    public <T extends LicenseRequestFiltering> T setRelatedTenant(List<SecurityTenant> relatedTenant) {
        this.relatedTenant = relatedTenant;
        return (T) this;
    }

    public Set<String> getRelatedTenantsIds() {
        return relatedTenantsIds;
    }

    public <T extends LicenseRequestFiltering> T setRelatedTenantsIds(Set<String> relatedTenantsIds) {
        this.relatedTenantsIds = relatedTenantsIds;
        return (T) this;
    }

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends LicenseRequestFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }
}
