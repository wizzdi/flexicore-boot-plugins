package com.wizzdi.maps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Layer extends SecuredBasic {
    int ordinal;
    @JsonIgnore
    @OneToMany(targetEntity = MappedPOI.class,mappedBy = "layer")
    private List<MappedPOI> mappedPOIS=new ArrayList<>();
    private String externalId;
    @ManyToOne(targetEntity = LayerType.class)
    private LayerType layerType;
    public int getOrdinal() {
        return ordinal;
    }

    public Layer setOrdinal(int ordinal) {
        this.ordinal = ordinal;
        return this;
    }

    public List<MappedPOI> getMappedPOIS() {
        return mappedPOIS;
    }

    public Layer setMappedPOIS(List<MappedPOI> mappedPOIS) {
        this.mappedPOIS = mappedPOIS;
        return this;
    }
    @ManyToOne(targetEntity = LayerType.class)
    public LayerType getLayerType() {
        return layerType;
    }

    public Layer setLayerType(LayerType layerType) {
        this.layerType = layerType;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public Layer setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }
}
