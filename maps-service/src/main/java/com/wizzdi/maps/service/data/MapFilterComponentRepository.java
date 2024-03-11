package com.wizzdi.maps.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.MappedPOI_;
import com.wizzdi.maps.service.request.MapFilterComponentRequest;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Extension
@Component
public class MapFilterComponentRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private MappedPOIRepository mappedPOIRepository;
    @Autowired
    private BasicRepository basicRepository;

    /**
     * @param mapFilterComponentRequest
     * @param securityContext
     * @return List of MappedPOI
     */
    public List<Tuple> listAllMapFilterComponents(MapFilterComponentRequest mapFilterComponentRequest, SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<MappedPOI> r = q.from(MappedPOI.class);
        List<Predicate> preds = new ArrayList<>();
        addMapFilterComponentsPredicates(mapFilterComponentRequest, cb, q, r, preds, securityContext);

        Path<?> objectPath = mapFilterComponentRequest.getFilterComponentPropertyProvider().getPropertyPath(r,cb , q, preds, mapFilterComponentRequest);
        BasicPropertiesFilter basicPropertiesFilter = mapFilterComponentRequest.getBasicPropertiesFilter();

        Class<?> entity = mapFilterComponentRequest.getFilterComponentPropertyProvider().getType(mapFilterComponentRequest);
        boolean basic = Basic.class.isAssignableFrom(entity);

        if(basicPropertiesFilter!=null){
            if(basic){
                BasicRepository.addBasicPropertiesFilter(basicPropertiesFilter,cb,q,(From<?, ? extends Basic>) objectPath,preds );
            }
            if(String.class.equals(entity)&&basicPropertiesFilter.getNameLike()!=null){
                Expression<String> ex= (Expression<String>) objectPath;
                preds.add(cb.like(ex,basicPropertiesFilter.getNameLike()));
            }

        }





        q.select(cb.tuple(objectPath,cb.count(r.get(MappedPOI_.id)))).where(preds.toArray(new Predicate[0])).groupBy(objectPath);
        if(basic){
            q.orderBy(cb.asc(objectPath.get(SecuredBasic_.name.getName())));
        }
        else{
            if(String.class.equals(entity)){
                q.orderBy(cb.asc(objectPath));

            }
        }
        TypedQuery<Tuple> query = em.createQuery(q);
        BasicRepository.addPagination(mapFilterComponentRequest, query);
        return query.getResultList();
    }

    public <T extends MappedPOI> void addMapFilterComponentsPredicates(
            MapFilterComponentRequest mapFilterComponentRequest,
            CriteriaBuilder cb,
            CriteriaQuery<?> q,
            From<?, T> r,
            List<Predicate> preds,
            SecurityContextBase securityContext) {
        MappedPOIFilter mappedPOIFilter = mapFilterComponentRequest.getMappedPOIFilter();
        mappedPOIRepository.addMappedPOIPredicate(mappedPOIFilter,cb,q,r,preds,securityContext);
        if(mapFilterComponentRequest.getPredicateAdder()!=null){
            mapFilterComponentRequest.getPredicateAdder().addPredicates(mapFilterComponentRequest,cb,q,r,preds,securityContext);
        }


    }

    /**
     * @param mapFilterComponentRequest
     * @param securityContext
     * @return count of MappedPOI
     */
    public <T> Long countAllMapFilterComponents(MapFilterComponentRequest mapFilterComponentRequest, SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<MappedPOI> r = q.from(MappedPOI.class);
        List<Predicate> preds = new ArrayList<>();
        addMapFilterComponentsPredicates(mapFilterComponentRequest, cb, q, r, preds, securityContext);
        Path<?> objectPath = mapFilterComponentRequest.getFilterComponentPropertyProvider().getPropertyPath(r,cb , q, preds, mapFilterComponentRequest);
        BasicPropertiesFilter basicPropertiesFilter = mapFilterComponentRequest.getBasicPropertiesFilter();

        Class<?> entity = mapFilterComponentRequest.getFilterComponentPropertyProvider().getType(mapFilterComponentRequest);
        boolean basic = Basic.class.isAssignableFrom(entity);

        if(basicPropertiesFilter!=null){
            if(basic){
                BasicRepository.addBasicPropertiesFilter(basicPropertiesFilter,cb,q,(From<?, ? extends Basic>) objectPath,preds );
            }
            if(String.class.equals(entity)&&basicPropertiesFilter.getNameLike()!=null){
                Expression<String> ex= (Expression<String>) objectPath;
                preds.add(cb.like(ex,basicPropertiesFilter.getNameLike()));
            }

        }
        q.select(cb.countDistinct(objectPath)).where(preds.toArray(new Predicate[0]));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }


    public <T extends Baseclass> List<T> listByIds(
            Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return mappedPOIRepository.listByIds(c, ids, securityContext);
    }


    public <T extends Baseclass> T getByIdOrNull(
            String id, Class<T> c, SecurityContextBase securityContext) {
        return mappedPOIRepository.getByIdOrNull(id, c, securityContext);
    }


    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
            String id,
            Class<T> c,
            SingularAttribute<D, E> baseclassAttribute,
            SecurityContextBase securityContext) {
        return mappedPOIRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }


    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
            Class<T> c,
            Set<String> ids,
            SingularAttribute<D, E> baseclassAttribute,
            SecurityContextBase securityContext) {
        return mappedPOIRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }


    public <D extends Basic, T extends D> List<T> findByIds(
            Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return mappedPOIRepository.findByIds(c, ids, idAttribute);
    }


    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return mappedPOIRepository.findByIds(c, requested);
    }


    public <T> T findByIdOrNull(Class<T> type, String id) {
        return mappedPOIRepository.findByIdOrNull(type, id);
    }


    @Transactional
    public void merge(Object base) {
        mappedPOIRepository.merge(base);
    }


    @Transactional
    public void massMerge(List<?> toMerge) {
        mappedPOIRepository.massMerge(toMerge);
    }

}
