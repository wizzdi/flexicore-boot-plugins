package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Organization;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class CustomerCreate extends BasicCreate {

    private String externalId;

    public String getExternalId() {
        return externalId;
    }

    public <T extends CustomerCreate> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }
}
