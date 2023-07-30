package com.flexicore.license.holders;

import com.flexicore.license.model.LicenseRequestToEntity;

/**
 * Created by Asaf on 18/10/2016.
 */
public class RequestToFeatureEntityHolder extends RequestToLicenseEntityHolder{


    public RequestToFeatureEntityHolder() {
    }

    public RequestToFeatureEntityHolder(LicenseRequestToEntity licenseRequestToEntity) {
        super(licenseRequestToEntity);
    }
}
