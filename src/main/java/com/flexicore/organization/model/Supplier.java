package com.flexicore.organization.model;

import javax.persistence.Entity;


@Entity
public class Supplier extends Organization {
    static Supplier s_Singleton = new Supplier();

    public static Supplier s() {
        return s_Singleton;
    }

}
