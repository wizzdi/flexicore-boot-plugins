package com.wizzdi.maps.service.request;

import jakarta.persistence.criteria.*;
import java.util.List;

public interface FilterComponentPropertyProvider {

    Path<?> getPropertyPath(Root<?> r, CriteriaBuilder cb, CriteriaQuery<?> q, List<Predicate> predicates, MapFilterComponentRequest filter);

    Class<?> getType(MapFilterComponentRequest filter);
}
