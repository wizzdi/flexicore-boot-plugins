package com.flexicore.billing.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;

@Entity
public class BusinessService extends Baseclass {

    public BusinessService() {
    }

    public BusinessService(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }
}
