package com.flexicore.territories.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.*;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.territories.request.CityFilter;
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
public class CityRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private BaseclassRepository baseclassRepository;
	public List<City> listAllCities(SecurityContext SecurityContext,
			CityFilter filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<City> q = cb.createQuery(City.class);
		Root<City> r = q.from(City.class);
		List<Predicate> preds = new ArrayList<>();
		addCityPredicate(filtering,q, cb, r, preds,SecurityContext);
		q.select(r).where(preds.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(City_.name)));
		TypedQuery<City> query = em.createQuery(q);
		BasicRepository.addPagination(filtering,query);
		return query.getResultList();

	}

	public long countAllCities(SecurityContext SecurityContext,
			CityFilter filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<City> r = q.from(City.class);
		List<Predicate> preds = new ArrayList<>();
		addCityPredicate(filtering,q, cb, r, preds,SecurityContext);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();

	}

	private void addCityPredicate(CityFilter filtering, CommonAbstractCriteria q, CriteriaBuilder cb,
								  Root<City> r, List<Predicate> preds, SecurityContext SecurityContext) {

		if (filtering.getCountries() != null && !filtering.getCountries().isEmpty()) {
			Set<String> ids=filtering.getCountries().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<City, Country> countryJoin=r.join(City_.country);
			preds.add(countryJoin.get(Country_.id).in(ids));
		}

		if (filtering.getStates() != null && !filtering.getStates().isEmpty()) {
			Set<String> ids=filtering.getStates().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<City, State> stateJoin=r.join(City_.state);
			preds.add(stateJoin.get(State_.id).in(ids));
		}
		if(filtering.getBasicPropertiesFilter()!=null){
			BasicRepository.addBasicPropertiesFilter(filtering.getBasicPropertiesFilter(),cb,q,r,preds);
		}
		if(filtering.getExternalIds()!=null&&!filtering.getExternalIds().isEmpty()){
			preds.add(r.get(City_.externalId).in(filtering.getExternalIds()));
		}
		if(SecurityContext!=null){
			baseclassRepository.addBaseclassPredicates(cb,q,r,preds,SecurityContext);
		}

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

	public void remove(Object o) {
		em.remove(o);
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

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return baseclassRepository.getByIdOrNull(id, c, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return baseclassRepository.listByIds(c, ids, securityContext);
	}
}
