package com.flexicore.license.request;


import com.flexicore.license.request.LicenseRequestToFeatureCreate;

/**
 * Created by Asaf on 17/10/2016.
 */
public class LicenseRequestToQuantityFeatureCreate extends LicenseRequestToFeatureCreate {

    private Integer quantityLimit;

    public Integer getQuantityLimit() {
        return quantityLimit;
    }

    public <T extends LicenseRequestToQuantityFeatureCreate> T setQuantityLimit(Integer quantityLimit) {
        this.quantityLimit = quantityLimit;
        return (T) this;
    }
}
