package com.flexicore.organization.model;

import com.flexicore.model.User;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class Employee extends User {
    static Employee s_Singleton = new Employee();

    public static Employee s() {
        return s_Singleton;
    }

    @ManyToOne(targetEntity = Organization.class)
    private Organization organization;

    @ManyToOne(targetEntity = Organization.class)
    public Organization getOrganization() {
        return organization;
    }

    public Employee setOrganization(Organization organization) {
        this.organization = organization;
        return this;
    }
}
