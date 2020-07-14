package com.flexicore.billing.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;

@Entity
public class PaymentMethodType extends Baseclass {

    private String canonicalClassName;

    public PaymentMethodType() {
    }

    public PaymentMethodType(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    public String getCanonicalClassName() {
        return canonicalClassName;
    }

    public <T extends PaymentMethodType> T setCanonicalClassName(String canonicalClassName) {
        this.canonicalClassName = canonicalClassName;
        return (T) this;
    }
}
