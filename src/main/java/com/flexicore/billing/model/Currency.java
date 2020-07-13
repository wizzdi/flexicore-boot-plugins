package com.flexicore.billing.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;

@Entity
public class Currency extends Baseclass {

    public Currency() {
    }

    public Currency(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }
}
