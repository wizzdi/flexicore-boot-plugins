package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.Layer;
import com.wizzdi.maps.model.MappedPOI;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@com.wizzdi.flexicore.security.validation.IdValid.List({
        @IdValid(
                targetField = "mappedPOIs",
                field = "mappedPOIIds",
                fieldType = com.wizzdi.maps.model.MappedPOI.class),
        @IdValid(
                targetField = "layers",
                field = "layerIds",
                fieldType = com.wizzdi.maps.model.Layer.class)
})
public class MappedPOIToLayerFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> mappedPOIIds = new HashSet<>();
    @JsonIgnore
    @TypeRetention(MappedPOI.class)
    private List<MappedPOI> mappedPOIs;
    private boolean mappedPOIExclude;
    private Set<String> layerIds = new HashSet<>();
    @JsonIgnore
    @TypeRetention(Layer.class)
    private List<Layer> layers;
    private boolean layerExclude;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends MappedPOIToLayerFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getMappedPOIIds() {
        return mappedPOIIds;
    }

    public <T extends MappedPOIToLayerFilter> T setMappedPOIIds(Set<String> mappedPOIIds) {
        this.mappedPOIIds = mappedPOIIds;
        return (T) this;
    }

    @JsonIgnore
    public List<MappedPOI> getMappedPOIs() {
        return mappedPOIs;
    }

    public <T extends MappedPOIToLayerFilter> T setMappedPOIs(List<MappedPOI> mappedPOIs) {
        this.mappedPOIs = mappedPOIs;
        return (T) this;
    }

    public boolean isMappedPOIExclude() {
        return mappedPOIExclude;
    }

    public <T extends MappedPOIToLayerFilter> T setMappedPOIExclude(boolean mappedPOIExclude) {
        this.mappedPOIExclude = mappedPOIExclude;
        return (T) this;
    }

    public Set<String> getLayerIds() {
        return layerIds;
    }

    public <T extends MappedPOIToLayerFilter> T setLayerIds(Set<String> layerIds) {
        this.layerIds = layerIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Layer> getLayers() {
        return layers;
    }

    public <T extends MappedPOIToLayerFilter> T setLayers(List<Layer> layers) {
        this.layers = layers;
        return (T) this;
    }

    public boolean isLayerExclude() {
        return layerExclude;
    }

    public <T extends MappedPOIToLayerFilter> T setLayerExclude(boolean layerExclude) {
        this.layerExclude = layerExclude;
        return (T) this;
    }
}
