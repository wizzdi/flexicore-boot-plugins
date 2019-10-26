package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Presenter;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.security.SecurityContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Preset extends Presenter {
    static Preset s_Singleton = new Preset();
    public static Preset s() { return s_Singleton; }

    public Preset() {
    }

    public Preset(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }
}
