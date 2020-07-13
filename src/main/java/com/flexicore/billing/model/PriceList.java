package com.flexicore.billing.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;

@Entity
public class PriceList extends Baseclass {

    public PriceList() {
    }

    public PriceList(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }
}
