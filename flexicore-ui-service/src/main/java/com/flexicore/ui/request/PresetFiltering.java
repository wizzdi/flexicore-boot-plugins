package com.flexicore.ui.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;


import java.util.Set;

public class PresetFiltering extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> externalIds;
    @JsonIgnore
    private Set<String> relatedBaseclass;

    public Set<String> getExternalIds() {
        return externalIds;
    }

    public <T extends PresetFiltering> T setExternalIds(Set<String> externalIds) {
        this.externalIds = externalIds;
        return (T) this;
    }

    @JsonIgnore
    public Set<String> getRelatedBaseclass() {
        return relatedBaseclass;
    }

    public <T extends PresetFiltering> T setRelatedBaseclass(Set<String> relatedBaseclass) {
        this.relatedBaseclass = relatedBaseclass;
        return (T) this;
    }

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends PresetFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }
}
