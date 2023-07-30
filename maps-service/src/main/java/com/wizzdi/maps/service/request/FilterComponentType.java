package com.wizzdi.maps.service.request;

import com.flexicore.model.Baseclass_;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.territories.*;
import com.wizzdi.maps.model.*;

import jakarta.persistence.metamodel.Attribute;
import java.util.Arrays;
import java.util.List;

public enum FilterComponentType {
    COUNTRY(Country.class, Arrays.asList(MappedPOI_.address, Address_.street, Street_.city, City_.country)),
    CITY(City.class, Arrays.asList(MappedPOI_.address, Address_.street, Street_.city)),
    STREET(Street.class, Arrays.asList(MappedPOI_.address, Address_.street)),
    NEIGHBOURHOOD(Neighbourhood.class, Arrays.asList(MappedPOI_.address, Address_.neighbourhood)),
    STATE(State.class, Arrays.asList(MappedPOI_.address, Address_.street, Street_.city, City_.state)),
    MAP_ICON(MapIcon.class, Arrays.asList(MappedPOI_.mapIcon)),
    MAP_GROUPS(MapGroup.class, Arrays.asList(MappedPOI_.mapGroupToMappedPOIS, MapGroupToMappedPOI_.mapGroup)),
    TENANTS(SecurityTenant.class, Arrays.asList(MappedPOI_.security, Baseclass_.tenant)),
    ROOM(Room.class, Arrays.asList(MappedPOI_.room)),
    RELATED_TYPE(String.class, Arrays.asList(MappedPOI_.relatedType)),
    EXTERNAL_ID(String.class, Arrays.asList(MappedPOI_.externalId)),
    LAYER(Layer.class,Arrays.asList(MappedPOI_.layer)),
    BUILDING_FLOOR(BuildingFloor.class,Arrays.asList(MappedPOI_.buildingFloor));

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
