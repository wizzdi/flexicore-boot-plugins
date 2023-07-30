package com.wizzdi.maps.service.data;

import com.flexicore.model.Basic;
import com.flexicore.model.Basic_;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Set;

@Component
@Extension
public class MappedPOIRelatedRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;

    public <T extends Basic> List<T> getRelatedForType(Class<T> type, Set<String> ids){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(type);
        Root<T> r = q.from(type);

        q.select(r).where(r.get(Basic_.id).in(ids));
        TypedQuery<T> query = em.createQuery(q);
        return query.getResultList();
    }
}
