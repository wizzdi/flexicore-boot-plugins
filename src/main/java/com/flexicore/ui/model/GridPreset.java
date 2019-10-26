package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.security.SecurityContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;


@Entity
public class GridPreset extends Preset {
    static GridPreset s_Singleton = new GridPreset();
    public static GridPreset s() {
        return s_Singleton;
    }

    public GridPreset() {
    }

    public GridPreset(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    private String relatedClassCanonicalName;

    @ManyToOne(targetEntity = DynamicExecution.class)
    private DynamicExecution dynamicExecution;



    @OneToMany(targetEntity = UiField.class,mappedBy = "preset",cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JsonIgnore
    private List<UiField> uiFields=new ArrayList<>();


    public String getRelatedClassCanonicalName() {
        return relatedClassCanonicalName;
    }

    public GridPreset setRelatedClassCanonicalName(String relatedClassCanonicalName) {
        this.relatedClassCanonicalName = relatedClassCanonicalName;
        return this;
    }

    @OneToMany(targetEntity = UiField.class,mappedBy = "preset",cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JsonIgnore
    public List<UiField> getUiFields() {
        return uiFields;
    }

    public GridPreset setUiFields(List<UiField> uiFields) {
        this.uiFields = uiFields;
        return this;
    }


    @ManyToOne(targetEntity = DynamicExecution.class)
    public DynamicExecution getDynamicExecution() {
        return dynamicExecution;
    }

    public <T extends Preset> T setDynamicExecution(DynamicExecution dynamicExecution) {
        this.dynamicExecution = dynamicExecution;
        return (T) this;
    }
}
