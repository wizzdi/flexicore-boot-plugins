package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.Layer;
import com.wizzdi.maps.model.MappedPOI;

@com.wizzdi.flexicore.security.validation.IdValid.List({
        @IdValid(
                targetField = "mappedPOI",
                field = "mappedPOIId",
                fieldType = com.wizzdi.maps.model.MappedPOI.class,
                groups = {
                        com.wizzdi.flexicore.security.validation.Create.class,
                        com.wizzdi.flexicore.security.validation.Update.class
                }),
        @IdValid(
                targetField = "layer",
                field = "layerId",
                fieldType = com.wizzdi.maps.model.Layer.class,
                groups = {
                        com.wizzdi.flexicore.security.validation.Create.class,
                        com.wizzdi.flexicore.security.validation.Update.class
                })
})
public class MappedPOIToLayerCreate extends BasicCreate {

    private String mappedPOIId;
    @JsonIgnore
    private MappedPOI mappedPOI;
    private String layerId;
    @JsonIgnore
    private Layer layer;

    public String getMappedPOIId() {
        return mappedPOIId;
    }

    public <T extends MappedPOIToLayerCreate> T setMappedPOIId(String mappedPOIId) {
        this.mappedPOIId = mappedPOIId;
        return (T) this;
    }

    @JsonIgnore
    public MappedPOI getMappedPOI() {
        return mappedPOI;
    }

    public <T extends MappedPOIToLayerCreate> T setMappedPOI(MappedPOI mappedPOI) {
        this.mappedPOI = mappedPOI;
        return (T) this;
    }

    public String getLayerId() {
        return layerId;
    }

    public <T extends MappedPOIToLayerCreate> T setLayerId(String layerId) {
        this.layerId = layerId;
        return (T) this;
    }

    @JsonIgnore
    public Layer getLayer() {
        return layer;
    }

    public <T extends MappedPOIToLayerCreate> T setLayer(Layer layer) {
        this.layer = layer;
        return (T) this;
    }
}
