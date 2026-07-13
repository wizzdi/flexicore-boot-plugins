package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.List;

@Entity
public class UiStyle extends Baseclass {
    private String element;
    @Transient
    private List<UiStyleProperty> properties = new ArrayList<>();

    public UiStyle() {
    }

    @Transient
    public List<UiStyleProperty> getProperties() {
        return properties;
    }

    public <T extends UiStyle> T setProperties(List<UiStyleProperty> properties) {
        this.properties = properties != null ? new ArrayList<>(properties) : new ArrayList<>();
        return (T) this;
    }

    public String getElement() {
        return element;
    }

    public UiStyle setElement(String element) {
        this.element = element;
        return this;
    }
}
