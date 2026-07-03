package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MappedPOIToLayer;

@com.wizzdi.flexicore.security.validation.IdValid.List({
        @IdValid(
                targetField = "mappedPOIToLayer",
                field = "id",
                fieldType = com.wizzdi.maps.model.MappedPOIToLayer.class,
                groups = {com.wizzdi.flexicore.security.validation.Update.class})
})
public class MappedPOIToLayerUpdate extends MappedPOIToLayerCreate {

    private String id;
    @JsonIgnore
    private MappedPOIToLayer mappedPOIToLayer;

    public String getId() {
        return id;
    }

    public <T extends MappedPOIToLayerUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    @JsonIgnore
    public MappedPOIToLayer getMappedPOIToLayer() {
        return mappedPOIToLayer;
    }

    public <T extends MappedPOIToLayerUpdate> T setMappedPOIToLayer(MappedPOIToLayer mappedPOIToLayer) {
        this.mappedPOIToLayer = mappedPOIToLayer;
        return (T) this;
    }
}
