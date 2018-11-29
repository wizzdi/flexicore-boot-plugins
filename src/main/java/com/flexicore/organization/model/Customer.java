package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;

import javax.persistence.Entity;


@Entity
public class Customer extends Organization {
    static Customer s_Singleton = new Customer();

    public static Customer s() {
        return s_Singleton;
    }

}
