package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.LayerType;
@com.wizzdi.flexicore.security.validation.IdValid.List({
        @IdValid(
                targetField = "layerType",
                field = "layerTypeId",
                fieldType = com.wizzdi.maps.model.LayerType.class)})
public class LayerCreate extends BasicCreate {
    private String LayerTypeId;
    private String externalId;
    @JsonIgnore
    private LayerType layerType;

    public LayerType getLayerType() {
        return layerType;
    }

    public LayerCreate setLayerType(LayerType layerType) {
        this.layerType = layerType;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public LayerCreate setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }
}
