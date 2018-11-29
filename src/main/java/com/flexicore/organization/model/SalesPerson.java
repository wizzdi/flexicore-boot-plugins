package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;

import javax.persistence.Entity;


@Entity
public class SalesPerson extends Baseclass {
    static SalesPerson s_Singleton = new SalesPerson();

    public static SalesPerson s() {
        return s_Singleton;
    }

}
