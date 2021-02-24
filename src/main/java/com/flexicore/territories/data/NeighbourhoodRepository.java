package com.flexicore.territories.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.City_;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Neighbourhood_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.territories.request.NeighbourhoodFilter;
import com.wizzdi.flexicore.boot.base.annotations.plugins.PluginInfo;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BaseclassRepository;
import com.wizzdi.flexicore.security.data.BasicRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
public class NeighbourhoodRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private BaseclassRepository baseclassRepository;

	public List<Neighbourhood> getAllNeighbourhoods(
			SecurityContextBase securityContextBase, NeighbourhoodFilter filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Neighbourhood> q = cb.createQuery(Neighbourhood.class);
		Root<Neighbourhood> r = q.from(Neighbourhood.class);
		List<Predicate> preds = new ArrayList<>();
		addNeighbourhoodPredicate(filtering,q, cb, r, preds,securityContextBase);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<Neighbourhood> query = em.createQuery(q);
		BasicRepository.addPagination(filtering,query);
		return query.getResultList();

	}

	public long countAllNeighbourhoods(SecurityContextBase securityContextBase,
			NeighbourhoodFilter filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Neighbourhood> r = q.from(Neighbourhood.class);
		List<Predicate> preds = new ArrayList<>();
		addNeighbourhoodPredicate(filtering,q, cb, r, preds,securityContextBase);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();

	}

	private void addNeighbourhoodPredicate(NeighbourhoodFilter filtering,
										   CommonAbstractCriteria q,
										   CriteriaBuilder cb, Root<Neighbourhood> r, List<Predicate> preds, SecurityContextBase securityContextBase) {

		if (filtering.getExternalIds() != null && !filtering.getExternalIds().isEmpty()) {
			preds.add(r.get(Neighbourhood_.externalId).in(filtering.getExternalIds()));
		}
		if (filtering.getCities() != null && !filtering.getCities().isEmpty()) {
			Set<String> ids=filtering.getCities().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<Neighbourhood, City> countryJoin=r.join(Neighbourhood_.city);
			preds.add(countryJoin.get(City_.id).in(ids));
		}
		if(filtering.getBasicPropertiesFilter()!=null){
			BasicRepository.addBasicPropertiesFilter(filtering.getBasicPropertiesFilter(),cb,q,r,preds);
		}
		if(securityContextBase!=null){
			Join<Neighbourhood, Baseclass> join=r.join(Neighbourhood_.security);
			baseclassRepository.addBaseclassPredicates(cb,q,join,preds,securityContextBase);
		}

	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return baseclassRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return baseclassRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return baseclassRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return baseclassRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return baseclassRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		baseclassRepository.merge(base);
	}

	@Transactional
	public void merge(Object base, boolean updateDate) {
		baseclassRepository.merge(base, updateDate);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		baseclassRepository.massMerge(toMerge);
	}

	@Transactional
	public void massMerge(List<?> toMerge, boolean updatedate) {
		baseclassRepository.massMerge(toMerge, updatedate);
	}

	public void remove(Object o) {
		em.remove(o);
	}
}