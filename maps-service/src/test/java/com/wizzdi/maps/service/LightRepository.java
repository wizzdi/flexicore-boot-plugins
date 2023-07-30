package com.wizzdi.maps.service;

import com.flexicore.model.Basic_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LightRepository {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;


    public <T extends Light> void addLightPredicate(
            LightFilter lightFilter,
            CriteriaBuilder cb,
            CommonAbstractCriteria q,
            From<?, T> r,
            List<Predicate> preds,
            SecurityContextBase securityContext) {

        this.securedBasicRepository.addSecuredBasicPredicates(
                lightFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

        if (lightFilter.getLightOperators() != null && !lightFilter.getLightOperators().isEmpty()) {
            Set<String> ids =
                    lightFilter.getLightOperators().parallelStream()
                            .map(f -> f.getId())
                            .collect(Collectors.toSet());
            Join<T, LightOperator> join = r.join("lightOperator");
            preds.add(join.get(Basic_.id).in(ids));
        }

    }
}
