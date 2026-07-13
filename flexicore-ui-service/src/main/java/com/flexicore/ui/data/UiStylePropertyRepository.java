package com.flexicore.ui.data;

import com.flexicore.model.Baseclass_;
import com.flexicore.ui.model.UiStyle;
import com.flexicore.ui.model.UiStyleProperty;
import com.flexicore.ui.model.UiStyleProperty_;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Extension
@Component
public class UiStylePropertyRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;

    public List<UiStyleProperty> listByUiStyles(Collection<UiStyle> uiStyles) {
        if (uiStyles == null || uiStyles.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> styleIds = uiStyles.stream()
                .map(UiStyle::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (styleIds.isEmpty()) {
            return Collections.emptyList();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UiStyleProperty> queryDefinition = cb.createQuery(UiStyleProperty.class);
        Root<UiStyleProperty> root = queryDefinition.from(UiStyleProperty.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(root.get(UiStyleProperty_.uiStyle).get(Baseclass_.id).in(styleIds));
        predicates.add(cb.isFalse(root.get(Baseclass_.softDelete)));
        queryDefinition.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(
                        cb.asc(root.get(UiStyleProperty_.uiStyle).get(Baseclass_.id)),
                        cb.asc(root.get(UiStyleProperty_.priority)),
                        cb.asc(root.get(Baseclass_.id)));
        TypedQuery<UiStyleProperty> query = em.createQuery(queryDefinition);
        return query.getResultList();
    }
}
