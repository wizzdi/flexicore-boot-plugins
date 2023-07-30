package com.wizzdi.maps.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.List;

public class LightFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    @JsonIgnore
    private List<LightOperator> lightOperators;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends LightFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    @JsonIgnore
    public List<LightOperator> getLightOperators() {
        return lightOperators;
    }

    public <T extends LightFilter> T setLightOperators(List<LightOperator> lightOperators) {
        this.lightOperators = lightOperators;
        return (T) this;
    }
}
