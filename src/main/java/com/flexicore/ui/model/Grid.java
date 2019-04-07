package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Grid extends Baseclass {
    static Grid s_Singleton = new Grid();
    public static Grid s() {
        return s_Singleton;
    }

    private String relatedClassCanonicalName;

    @OneToMany(targetEntity = UiField.class,mappedBy = "preset",cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JsonIgnore
    private List<UiField> uiFields=new ArrayList<>();


    public String getRelatedClassCanonicalName() {
        return relatedClassCanonicalName;
    }

    public Grid setRelatedClassCanonicalName(String relatedClassCanonicalName) {
        this.relatedClassCanonicalName = relatedClassCanonicalName;
        return this;
    }

    @OneToMany(targetEntity = UiField.class,mappedBy = "preset",cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JsonIgnore
    public List<UiField> getUiFields() {
        return uiFields;
    }

    public Grid setUiFields(List<UiField> uiFields) {
        this.uiFields = uiFields;
        return this;
    }
}
