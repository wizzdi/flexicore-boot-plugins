package com.flexicore.organization.model;

import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class OrganizationalCustomer extends Customer{

    @ManyToOne(targetEntity = Organization.class)
    private Organization organization;


    public OrganizationalCustomer() {
    }

    public OrganizationalCustomer(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    @ManyToOne(targetEntity = Organization.class)
    public Organization getOrganization() {
        return organization;
    }

    public <T extends OrganizationalCustomer> T setOrganization(Organization organization) {
        this.organization = organization;
        return (T) this;
    }

}
