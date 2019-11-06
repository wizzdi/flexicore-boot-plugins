package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.model.dynamic.DynamicInvoker;
import com.flexicore.security.SecurityContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Form extends Preset {
    static Form s_Singleton = new Form();
    public static Form s() {
        return s_Singleton;
    }

    public Form() {
    }

    public Form(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }


    @ManyToOne(targetEntity = DynamicExecution.class)
    private DynamicInvoker dynamicInvoker;


    @ManyToOne(targetEntity = DynamicExecution.class)
    public DynamicInvoker getDynamicInvoker() {
        return dynamicInvoker;
    }

    public <T extends Form> T setDynamicInvoker(DynamicInvoker dynamicInvoker) {
        this.dynamicInvoker = dynamicInvoker;
        return (T) this;
    }
}
