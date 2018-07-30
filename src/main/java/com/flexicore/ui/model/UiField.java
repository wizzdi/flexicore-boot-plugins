package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;

import javax.persistence.Entity;


@Entity
public class UiField extends Baseclass {
    static UiField s_Singleton = new UiField();
    public static UiField s() {
        return s_Singleton;
    }





}
