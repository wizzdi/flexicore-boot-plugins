package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.SecuredBasic;
import com.flexicore.security.SecurityContextBase;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class IndustryToCustomer extends SecuredBasic {

    @ManyToOne(targetEntity = Industry.class)
    private Industry industry;
    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;


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
