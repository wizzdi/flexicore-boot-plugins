package com.flexicore.billing.data;


import com.flexicore.billing.model.PriceList;
import com.flexicore.billing.request.PriceListFiltering;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.data.BasicRepository;
import javax.persistence.TypedQuery;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.flexicore.security.SecurityContextBase;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Extension
@Component
public class PriceListRepository implements Plugin{
@PersistenceContext
private EntityManager em;
@Autowired
private SecuredBasicRepository securedBasicRepository;

	public List<PriceList> getAllPriceLists(SecurityContextBase securityContext,
                                       PriceListFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PriceList> q = cb.createQuery(PriceList.class);
		Root<PriceList> r = q.from(PriceList.class);
		List<Predicate> preds = new ArrayList<>();
		addPriceListPredicates(filtering, cb,q, r, preds,securityContext);
q.select(r).where(preds.toArray(Predicate[]::new));
TypedQuery<PriceList> query=em.createQuery(q);
BasicRepository.addPagination(filtering,query);
return query.getResultList();
	}

	public long countAllPriceLists(SecurityContextBase securityContext,
			PriceListFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<PriceList> r = q.from(PriceList.class);
		List<Predicate> preds = new ArrayList<>();
		addPriceListPredicates(filtering, cb,q, r, preds,securityContext);
q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
TypedQuery<Long> query=em.createQuery(q);
return query.getSingleResult();
	}

	public <T extends PriceList> void  addPriceListPredicates(PriceListFiltering filtering,
                                     CriteriaBuilder cb, CommonAbstractCriteria q,  From<?,T> r, List<Predicate> preds, SecurityContextBase securityContext) {
		securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(), cb,q,r,preds,securityContext);


	}


	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return securedBasicRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return securedBasicRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return securedBasicRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return securedBasicRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return securedBasicRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return securedBasicRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		securedBasicRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		securedBasicRepository.massMerge(toMerge);
	}

}