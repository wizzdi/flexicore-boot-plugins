package com.flexicore.organization.model;

import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;

@Entity
public class IndividualCustomer extends Customer{

    private String externalId;

    public IndividualCustomer() {
    }

    public IndividualCustomer(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    public String getExternalId() {
        return externalId;
    }

    public <T extends IndividualCustomer> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }
}
