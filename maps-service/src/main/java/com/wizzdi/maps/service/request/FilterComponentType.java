package com.wizzdi.maps.service.request;

import com.flexicore.model.Baseclass_;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.territories.*;
import com.wizzdi.maps.model.*;

import jakarta.persistence.metamodel.Attribute;
import java.util.Arrays;
import java.util.List;

public enum FilterComponentType {
    COUNTRY(Country.class, List.of(MappedPOI_.address, Address_.street, Street_.city, City_.country)),
    CITY(City.class, List.of(MappedPOI_.address, Address_.street, Street_.city)),
    STREET(Street.class, List.of(MappedPOI_.address, Address_.street)),
    NEIGHBOURHOOD(Neighbourhood.class, List.of(MappedPOI_.address, Address_.neighbourhood)),
    STATE(State.class, List.of(MappedPOI_.address, Address_.street, Street_.city, City_.state)),
    MAP_ICON(MapIcon.class, List.of(MappedPOI_.mapIcon)),
    MAP_GROUPS(MapGroup.class, List.of(MappedPOI_.mapGroupToMappedPOIS, MapGroupToMappedPOI_.mapGroup)),
    TENANTS(SecurityTenant.class, List.of(MappedPOI_.security, Baseclass_.tenant)),
    ROOM(Room.class, List.of(MappedPOI_.room)),
    RELATED_TYPE(String.class, List.of(MappedPOI_.relatedType)),
    EXTERNAL_ID(String.class, List.of(MappedPOI_.externalId)),
    LAYER(Layer.class,List.of(MappedPOI_.layer)),
    BUILDING_FLOOR(BuildingFloor.class,List.of(MappedPOI_.room,Room_.buildingFloor)),
    MAPPED_POI_NAME(String.class,List.of(MappedPOI_.name));

    private final Class<?> entity;
    private final List<Attribute> attributes;


    FilterComponentType(Class<?> entity, List<Attribute> attributes) {
        this.entity = entity;
        this.attributes = attributes;
    }

    public Class<?> getEntity() {
        return entity;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
}
