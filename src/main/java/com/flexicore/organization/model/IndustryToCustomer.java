package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class IndustryToCustomer extends Baseclass {

    @ManyToOne(targetEntity = Industry.class)
    private Industry industry;
    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;

    public IndustryToCustomer() {
    }

    public IndustryToCustomer(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    @ManyToOne(targetEntity = Industry.class)
    public Industry getIndustry() {
        return industry;
    }

    public <T extends IndustryToCustomer> T setIndustry(Industry industry) {
        this.industry = industry;
        return (T) this;
    }

    @ManyToOne(targetEntity = Customer.class)
    public Customer getCustomer() {
        return customer;
    }

    public <T extends IndustryToCustomer> T setCustomer(Customer customer) {
        this.customer = customer;
        return (T) this;
    }
}
