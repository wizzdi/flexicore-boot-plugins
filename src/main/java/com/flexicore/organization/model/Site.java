package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.territories.Address;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Site extends Baseclass {
    static private Site s_Singleton = new Site();

    static public Site s() {
        return s_Singleton;
    }

    private String externalId;

    @ManyToOne(targetEntity = Address.class)
    private Address address;

    @ManyToOne(targetEntity = Address.class)
    public Address getAddress() {
        return address;
    }

    public Site setAddress(Address address) {
        this.address = address;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public Site setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }
}