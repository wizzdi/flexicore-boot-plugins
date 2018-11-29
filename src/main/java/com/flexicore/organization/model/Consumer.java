package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.User;

import javax.persistence.Entity;


@Entity
public class Consumer extends User {
    static Consumer s_Singleton = new Consumer();

    public static Consumer s() {
        return s_Singleton;
    }

}
