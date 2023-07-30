package com.wizzdi.maps.service.request;

import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Attribute;
import java.util.List;

public class StandardFilterComponentProvider implements FilterComponentPropertyProvider{

    @Override
    public Class<?> getType(MapFilterComponentRequest filter) {

        Class<?> entity = filter.getFilterComponentType().getEntity();
        return entity;
    }

    @Override
    public Path<?> getPropertyPath(Root<?> r, CriteriaBuilder cb, CriteriaQuery<?> q, List<Predicate> predicates, MapFilterComponentRequest filter) {
        List<Attribute> attributes=filter.getFilterComponentType().getAttributes();
        From<?,?> from=r;
        for (int i = 0; i < attributes.size()-1; i++) {
            Attribute attribute=attributes.get(i);
            Join<?,?> join=from.join(attribute.getName());
            from=join;
        }
        Attribute<?,?> lastOne=attributes.get(attributes.size()-1);
        return from.get(lastOne.getName());
    }
}
