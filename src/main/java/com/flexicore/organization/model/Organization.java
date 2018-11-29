package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;

import javax.persistence.Entity;


@Entity
public class Organization extends Baseclass {
    static Organization s_Singleton = new Organization();

    public static Organization s() {
        return s_Singleton;
    }

}
