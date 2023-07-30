package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

public class MapFilterComponentRequest extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private MappedPOIFilter mappedPOIFilter;

    private FilterComponentType filterComponentType;
    @JsonIgnore
    private boolean custom;
    @JsonIgnore
    private PredicateAdder<MapFilterComponentRequest> predicateAdder;
    @JsonIgnore
    private FilterComponentPropertyProvider filterComponentPropertyProvider;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends MapFilterComponentRequest> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public MappedPOIFilter getMappedPOIFilter() {
        return mappedPOIFilter;
    }

    public <T extends MapFilterComponentRequest> T setMappedPOIFilter(MappedPOIFilter mappedPOIFilter) {
        this.mappedPOIFilter = mappedPOIFilter;
        return (T) this;
    }

    public FilterComponentType getFilterComponentType() {
        return filterComponentType;
    }

    public <T extends MapFilterComponentRequest> T setFilterComponentType(FilterComponentType filterComponentType) {
        this.filterComponentType = filterComponentType;
        return (T) this;
    }

    @JsonIgnore
    public PredicateAdder<MapFilterComponentRequest> getPredicateAdder() {
        return predicateAdder;
    }

    public <T extends MapFilterComponentRequest> T setPredicateAdder(PredicateAdder<MapFilterComponentRequest> predicateAdder) {
        this.predicateAdder = predicateAdder;
        return (T) this;
    }

    @JsonIgnore
    public FilterComponentPropertyProvider getFilterComponentPropertyProvider() {
        return filterComponentPropertyProvider;
    }

    public <T extends MapFilterComponentRequest> T setFilterComponentPropertyProvider(FilterComponentPropertyProvider filterComponentPropertyProvider) {
        this.filterComponentPropertyProvider = filterComponentPropertyProvider;
        return (T) this;
    }

    @JsonIgnore
    public boolean isCustom() {
        return custom;
    }

    public <T extends MapFilterComponentRequest> T setCustom(boolean custom) {
        this.custom = custom;
        return (T) this;
    }

}
