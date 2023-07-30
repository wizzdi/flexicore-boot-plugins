package com.flexicore.license.service;

import com.flexicore.license.model.LicenseRequest;
import com.flexicore.security.SecurityContextBase;

public class LicenseRequestUpdateEvent {
    private LicenseRequest licenseRequest;
    private SecurityContextBase securityContext;

    public LicenseRequest getLicenseRequest() {
        return licenseRequest;
    }

    public <T extends LicenseRequestUpdateEvent> T setLicenseRequest(LicenseRequest licenseRequest) {
        this.licenseRequest = licenseRequest;
        return (T) this;
    }

    public SecurityContextBase getSecurityContextBase() {
        return securityContext;
    }

    public <T extends LicenseRequestUpdateEvent> T setSecurityContextBase(SecurityContextBase securityContext) {
        this.securityContext = securityContext;
        return (T) this;
    }
}
