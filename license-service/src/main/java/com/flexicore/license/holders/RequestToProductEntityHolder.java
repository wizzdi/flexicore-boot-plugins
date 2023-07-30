package com.flexicore.license.holders;

import com.flexicore.license.model.LicenseRequestToEntity;

/**
 * Created by Asaf on 18/10/2016.
 */
public class RequestToProductEntityHolder extends RequestToLicenseEntityHolder{


    public RequestToProductEntityHolder() {
    }

    public RequestToProductEntityHolder(LicenseRequestToEntity licenseRequestToEntity) {
        super(licenseRequestToEntity);
    }
}
