package com.wizzdi.maps.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "mapped_poi_to_layer_idx", columnList = "mappedPOI_id,layer_id")
})
public class MappedPOIToLayer extends Baseclass {

    @ManyToOne(targetEntity = MappedPOI.class)
    private MappedPOI mappedPOI;

    @ManyToOne(targetEntity = Layer.class)
    private Layer layer;

    @ManyToOne(targetEntity = MappedPOI.class)
    public MappedPOI getMappedPOI() {
        return mappedPOI;
    }

    public <T extends MappedPOIToLayer> T setMappedPOI(MappedPOI mappedPOI) {
        this.mappedPOI = mappedPOI;
        return (T) this;
    }

    @ManyToOne(targetEntity = Layer.class)
    public Layer getLayer() {
        return layer;
    }

    public <T extends MappedPOIToLayer> T setLayer(Layer layer) {
        this.layer = layer;
        return (T) this;
    }
}
