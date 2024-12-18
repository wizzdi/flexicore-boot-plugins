package com.wizzdi.maps.service;

import com.flexicore.model.Baseclass;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Light extends Baseclass {

    @ManyToOne(targetEntity = LightOperator.class)
    private LightOperator lightOperator;

    @ManyToOne(targetEntity = LightOperator.class)
    public LightOperator getLightOperator() {
        return lightOperator;
    }

    public <T extends Light> T setLightOperator(LightOperator lightOperator) {
        this.lightOperator = lightOperator;
        return (T) this;
    }
}
