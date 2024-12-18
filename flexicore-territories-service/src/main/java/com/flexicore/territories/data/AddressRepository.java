package com.flexicore.territories.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.*;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.territories.request.AddressFilter;
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
public class AddressRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private BaseclassRepository baseclassRepository;

	public List<Address> getAllAddresses(SecurityContext SecurityContext,
			AddressFilter filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Address> q = cb.createQuery(Address.class);
		Root<Address> r = q.from(Address.class);
		List<Predicate> preds = new ArrayList<>();
		addAddressPredicate(filtering,q, cb, r, preds,SecurityContext);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<Address> query = em.createQuery(q);
		BasicRepository.addPagination(filtering,query);
		return query.getResultList();

	}

	public long countAllAddresses(SecurityContext SecurityContext,
			AddressFilter filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Address> r = q.from(Address.class);
		List<Predicate> preds = new ArrayList<>();
		addAddressPredicate(filtering, q,cb, r, preds,SecurityContext);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));

		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();

	}

	public <T extends Address> void addAddressPredicate(AddressFilter filtering,
									 CommonAbstractCriteria q,
									 CriteriaBuilder cb, From<?,T> r, List<Predicate> preds, SecurityContext SecurityContext) {
		if (filtering.getExternalIds() != null
				&& !filtering.getExternalIds().isEmpty()) {
			preds.add(r.get(Address_.externalId).in(filtering.getExternalIds()));
		}
		if (filtering.getFloors() != null && !filtering.getFloors().isEmpty()) {
			preds.add(r.get(Address_.floorForAddress).in(filtering.getFloors()));
		}

		if (filtering.getZipCodes() != null
				&& !filtering.getZipCodes().isEmpty()) {
			preds.add(r.get(Address_.zipCode).in(filtering.getZipCodes()));
		}
		if (filtering.getStreets() != null && !filtering.getStreets().isEmpty()) {

			Set<String> ids = filtering.getStreets().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, Street> join = r.join(Address_.street);
			Predicate in = join.get(Street_.id).in(ids);
			preds.add(filtering.isStreetsExclude()?cb.not(in):in);
		}
		if (filtering.getCities() != null && !filtering.getCities().isEmpty()) {

			Set<String> ids = filtering.getCities().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, Street> join = r.join(Address_.street);
			Join<Street, City> join2 = join.join(Street_.city);
			Predicate in = join2.get(City_.id).in(ids);
			preds.add(filtering.isCitiesExclude()?cb.not(in):in);
		}
		if (filtering.getNeighbourhoods() != null && !filtering.getNeighbourhoods().isEmpty()) {

			Set<String> ids = filtering.getNeighbourhoods().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, Neighbourhood> join = r.join(Address_.neighbourhood);
			Predicate in = join.get(Neighbourhood_.id).in(ids);
			preds.add(filtering.isNeighbourhoodsExclude()?cb.not(in):in);
		}
		if (filtering.getStates() != null && !filtering.getStates().isEmpty()) {

			Set<String> ids = filtering.getCities().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, Street> join = r.join(Address_.street);
			Join<Street, City> join2 = join.join(Street_.city);
			Join<City, State> join3 = join2.join(City_.state);
			Predicate in = join3.get(State_.id).in(ids);
			preds.add(filtering.isStatesExclude()?cb.not(in):in);
		}
		if(filtering.getBasicPropertiesFilter()!=null){
			BasicRepository.addBasicPropertiesFilter(filtering.getBasicPropertiesFilter(),cb,q,r,preds);
		}
		if(SecurityContext!=null){
			baseclassRepository.addBaseclassPredicates(cb,q,r,preds,SecurityContext);
		}

	}

	public <T extends Address> List<T> findByIds(Class<T> c, Set<String> requested) {
		return baseclassRepository.findByIds(c,requested,Address_.id);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return baseclassRepository.getByIdOrNull(id, c, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return baseclassRepository.listByIds(c, ids, securityContext);
	}


	@Transactional
	public void merge(Object base) {
		baseclassRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		baseclassRepository.massMerge(toMerge);
	}
}
