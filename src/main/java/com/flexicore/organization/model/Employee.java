package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.User;

import javax.persistence.Entity;


@Entity
public class Employee extends User {
    static Employee s_Singleton = new Employee();

    public static Employee s() {
        return s_Singleton;
    }

}
