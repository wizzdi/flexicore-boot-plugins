package com.flexicore.territories.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.Country_;
import com.flexicore.model.territories.State;
import com.flexicore.model.territories.State_;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.territories.request.StateFilter;
import com.wizzdi.flexicore.boot.base.annotations.plugins.PluginInfo;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BaseclassRepository;
import com.wizzdi.flexicore.security.data.BasicRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
public class StateRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private BaseclassRepository baseclassRepository;

	public List<State> listAllStates(SecurityContext SecurityContext,
			StateFilter filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<State> q = cb.createQuery(State.class);
		Root<State> r = q.from(State.class);
		List<Predicate> preds = new ArrayList<>();
		addStatePredicate(filtering,q, cb, r, preds,SecurityContext);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<State> query = em.createQuery(q);
		BasicRepository.addPagination(filtering,query);
		return query.getResultList();

	}

	public long countAllStates(SecurityContext SecurityContext,
			StateFilter filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<State> r = q.from(State.class);
		List<Predicate> preds = new ArrayList<>();
		addStatePredicate(filtering,q, cb, r, preds,SecurityContext);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();

	}

	private void addStatePredicate(StateFilter filtering,
								   CommonAbstractCriteria q,
								   CriteriaBuilder cb, Root<State> r, List<Predicate> preds, SecurityContext SecurityContext) {
		if (filtering.getCountries() != null && !filtering.getCountries().isEmpty()) {
			Set<String> ids=filtering.getCountries().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<State, Country> countryJoin=r.join(State_.country);
			preds.add(countryJoin.get(Country_.id).in(ids));
		}
		if(filtering.getExternalIds()!=null&&!filtering.getExternalIds().isEmpty()){
			preds.add(r.get(State_.externalId).in(filtering.getExternalIds()));
		}
		if(filtering.getBasicPropertiesFilter()!=null){
			BasicRepository.addBasicPropertiesFilter(filtering.getBasicPropertiesFilter(),cb,q,r,preds);
		}
		if(SecurityContext!=null){
			baseclassRepository.addBaseclassPredicates(cb,q,r,preds,SecurityContext);
		}

	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return baseclassRepository.getByIdOrNull(id, c, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return baseclassRepository.listByIds(c, ids, securityContext);
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
