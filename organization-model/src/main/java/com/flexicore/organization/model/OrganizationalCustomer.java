package com.flexicore.organization.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class OrganizationalCustomer extends Customer{

    @ManyToOne(targetEntity = Organization.class)
    private Organization organization;

    @ManyToOne(targetEntity = Organization.class)
    public Organization getOrganization() {
        return organization;
    }

    public <T extends OrganizationalCustomer> T setOrganization(Organization organization) {
        this.organization = organization;
        return (T) this;
    }

}
