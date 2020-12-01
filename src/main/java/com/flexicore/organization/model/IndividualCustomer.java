package com.flexicore.organization.model;

import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;

@Entity
public class IndividualCustomer extends Customer{



    public IndividualCustomer() {
    }

    public IndividualCustomer(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

}
