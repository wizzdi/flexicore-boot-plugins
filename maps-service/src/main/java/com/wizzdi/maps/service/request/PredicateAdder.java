package com.wizzdi.maps.service.request;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.maps.model.MappedPOI;

import jakarta.persistence.criteria.*;
import java.util.List;

public interface PredicateAdder<E> {

     <T extends MappedPOI> void addPredicates(
            E mapFilterComponentRequest,
            CriteriaBuilder cb,
            CriteriaQuery<?> q,
            From<?, T> r,
            List<Predicate> preds,
            SecurityContextBase securityContext);
}
