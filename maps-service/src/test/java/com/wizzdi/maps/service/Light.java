package com.wizzdi.maps.service;

import com.flexicore.model.SecuredBasic;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Light extends SecuredBasic {

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
