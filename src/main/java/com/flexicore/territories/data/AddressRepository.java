package com.flexicore.territories.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.*;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.territories.request.AddressFilter;
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
public class AddressRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private BaseclassRepository baseclassRepository;

	public List<Address> getAllAddresses(SecurityContextBase securityContextBase,
			AddressFilter filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Address> q = cb.createQuery(Address.class);
		Root<Address> r = q.from(Address.class);
		List<Predicate> preds = new ArrayList<>();
		addAddressPredicate(filtering,q, cb, r, preds,securityContextBase);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<Address> query = em.createQuery(q);
		BasicRepository.addPagination(filtering,query);
		return query.getResultList();

	}

	public long countAllAddresses(SecurityContextBase securityContextBase,
			AddressFilter filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Address> r = q.from(Address.class);
		List<Predicate> preds = new ArrayList<>();
		addAddressPredicate(filtering, q,cb, r, preds,securityContextBase);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));

		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();

	}

	public <T extends Address> void addAddressPredicate(AddressFilter filtering,
									 CommonAbstractCriteria q,
									 CriteriaBuilder cb, From<?,T> r, List<Predicate> preds, SecurityContextBase securityContextBase) {
		if (filtering.getExternalIds() != null
				&& !filtering.getExternalIds().isEmpty()) {
			preds.add(r.get(Address_.externalId).in(filtering.getExternalIds()));
		}
		if (filtering.getFloors() != null && !filtering.getFloors().isEmpty()) {
			preds.add(r.get(Address_.floorForAddress).in(filtering.getFloors()));
		}
		if (filtering.getNumbers() != null && !filtering.getNumbers().isEmpty()) {
			preds.add(r.get(Address_.number).in(filtering.getNumbers()));
		}
		if (filtering.getZipCodes() != null
				&& !filtering.getZipCodes().isEmpty()) {
			preds.add(r.get(Address_.zipCode).in(filtering.getZipCodes()));
		}
		if (filtering.getStreets() != null && !filtering.getStreets().isEmpty()) {

			Set<String> ids = filtering.getStreets().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, Street> join = r.join(Address_.street);
			preds.add(join.get(Street_.id).in(ids));
		}
		if (filtering.getCities() != null && !filtering.getCities().isEmpty()) {

			Set<String> ids = filtering.getCities().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, Street> join = r.join(Address_.street);
			Join<Street, City> join2 = join.join(Street_.city);
			preds.add(join2.get(City_.id).in(ids));
		}
		if (filtering.getNeighbourhoods() != null && !filtering.getNeighbourhoods().isEmpty()) {

			Set<String> ids = filtering.getNeighbourhoods().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, Neighbourhood> join = r.join(Address_.neighbourhood);
			preds.add(join.get(Neighbourhood_.id).in(ids));
		}
		if(filtering.getBasicPropertiesFilter()!=null){
			BasicRepository.addBasicPropertiesFilter(filtering.getBasicPropertiesFilter(),cb,q,r,preds);
		}
		if(securityContextBase!=null){
			Join<T, Baseclass> join=r.join(Address_.security);
			baseclassRepository.addBaseclassPredicates(cb,q,join,preds,securityContextBase);
		}

	}

	public <T extends Address> List<T> findByIds(Class<T> c, Set<String> requested) {
		return baseclassRepository.findByIds(c,requested,Address_.id);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return baseclassRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return baseclassRepository.listByIds(c, ids, baseclassAttribute, securityContext);
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