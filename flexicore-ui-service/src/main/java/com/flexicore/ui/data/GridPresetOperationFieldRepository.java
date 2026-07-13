package com.flexicore.ui.data;

import com.flexicore.model.Baseclass_;
import com.flexicore.ui.model.GridPreset;
import com.flexicore.ui.model.GridPresetOperationField;
import com.flexicore.ui.model.GridPresetOperationField_;
import com.flexicore.ui.model.GridPreset_;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Extension
@Component
public class GridPresetOperationFieldRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;

    public List<GridPresetOperationField> listByGridPresets(Collection<GridPreset> gridPresets) {
        if (gridPresets == null || gridPresets.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> gridPresetIds = gridPresets.stream()
                .map(GridPreset::getId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        if (gridPresetIds.isEmpty()) {
            return Collections.emptyList();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GridPresetOperationField> q = cb.createQuery(GridPresetOperationField.class);
        Root<GridPresetOperationField> r = q.from(GridPresetOperationField.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(r.get(GridPresetOperationField_.gridPreset).get(GridPreset_.id).in(gridPresetIds));
        predicates.add(cb.isFalse(r.get(Baseclass_.softDelete)));
        q.select(r)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(
                        cb.asc(r.get(GridPresetOperationField_.gridPreset).get(GridPreset_.id)),
                        cb.asc(r.get(GridPresetOperationField_.operationType)),
                        cb.asc(r.get(GridPresetOperationField_.priority)),
                        cb.asc(r.get(Baseclass_.id)));
        TypedQuery<GridPresetOperationField> query = em.createQuery(q);
        return query.getResultList();
    }
}
