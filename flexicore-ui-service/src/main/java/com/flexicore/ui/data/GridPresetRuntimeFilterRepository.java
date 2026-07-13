package com.flexicore.ui.data;

import com.flexicore.model.Baseclass_;
import com.flexicore.ui.model.GridPreset;
import com.flexicore.ui.model.GridPresetRuntimeFilter;
import com.flexicore.ui.model.GridPresetRuntimeFilter_;
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
public class GridPresetRuntimeFilterRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;

    public List<GridPresetRuntimeFilter> listByGridPresets(Collection<GridPreset> gridPresets) {
        if (gridPresets == null || gridPresets.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> ids = gridPresets.stream().map(GridPreset::getId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GridPresetRuntimeFilter> q = cb.createQuery(GridPresetRuntimeFilter.class);
        Root<GridPresetRuntimeFilter> r = q.from(GridPresetRuntimeFilter.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(r.get(GridPresetRuntimeFilter_.gridPreset).get(GridPreset_.id).in(ids));
        predicates.add(cb.isFalse(r.get(Baseclass_.softDelete)));
        q.select(r).where(predicates.toArray(new Predicate[0])).orderBy(
                cb.asc(r.get(GridPresetRuntimeFilter_.gridPreset).get(GridPreset_.id)),
                cb.asc(r.get(GridPresetRuntimeFilter_.priority)),
                cb.asc(r.get(Baseclass_.id)));
        TypedQuery<GridPresetRuntimeFilter> query = em.createQuery(q);
        return query.getResultList();
    }
}
