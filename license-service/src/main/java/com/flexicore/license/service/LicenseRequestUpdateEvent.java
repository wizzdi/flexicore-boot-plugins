package com.flexicore.license.service;

import com.flexicore.license.model.LicenseRequest;
import com.wizzdi.flexicore.security.configuration.SecurityContext;

public class LicenseRequestUpdateEvent {
    private LicenseRequest licenseRequest;
    private SecurityContext securityContext;

    public LicenseRequest getLicenseRequest() {
        return licenseRequest;
    }

    public <T extends LicenseRequestUpdateEvent> T setLicenseRequest(LicenseRequest licenseRequest) {
        this.licenseRequest = licenseRequest;
        return (T) this;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    public <T extends LicenseRequestUpdateEvent> T setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
        return (T) this;
    }
}
