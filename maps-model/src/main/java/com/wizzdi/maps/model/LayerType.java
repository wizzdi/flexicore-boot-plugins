package com.wizzdi.maps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class LayerType extends SecuredBasic {
    @JsonIgnore
    @OneToMany(targetEntity = Layer.class,mappedBy = "layerType")
    private List<Layer> layers=new ArrayList<>();
    @OneToMany(targetEntity = Layer.class,mappedBy = "layerType")
    public List<Layer> getLayers() {
        return layers;
    }

    public LayerType setLayers(List<Layer> layers) {
        this.layers = layers;
        return this;
    }
}
