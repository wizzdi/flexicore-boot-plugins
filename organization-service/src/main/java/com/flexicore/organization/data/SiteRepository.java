package com.flexicore.organization.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.Address;
import com.flexicore.model.territories.Address_;
import com.flexicore.organization.model.Site;
import com.flexicore.organization.model.Site_;
import com.flexicore.organization.request.SiteFiltering;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
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


@Extension
@Component
public class SiteRepository implements Plugin {

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private SecuredBasicRepository securedBasicRepository;

	public List<Site> getAllSites(SecurityContext securityContext,
								  SiteFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Site> q = cb.createQuery(Site.class);
		Root<Site> r = q.from(Site.class);
		List<Predicate> preds = new ArrayList<>();
		addSitePredicates(filtering, cb,q,r, preds,securityContext);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<Site> query = em.createQuery(q);
		BasicRepository.addPagination(filtering, query);
		return query.getResultList();
	}

	public long countAllSites(SecurityContext securityContext,
							  SiteFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Site> r = q.from(Site.class);
		List<Predicate> preds = new ArrayList<>();
		addSitePredicates(filtering, cb,q,r, preds,securityContext);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();
	}


	public <T extends Site> void addSitePredicates(SiteFiltering filtering,
												   CriteriaBuilder cb,
												   CommonAbstractCriteria q,From<?,T> r, List<Predicate> preds,SecurityContext securityContext) {

		securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(),cb,q,r,preds,securityContext);
		if (filtering.getAddresses() != null
				&& !filtering.getAddresses().isEmpty()) {
			Set<String> ids = filtering.getAddresses().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, Address> join = r.join(Site_.address);
			preds.add(join.get(Address_.id).in(ids));
		}

		if (filtering.getExternalIds() != null
				&& !filtering.getExternalIds().isEmpty()) {
			preds.add(r.get(Site_.externalId).in(filtering.getExternalIds()));
		}

	}


	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return securedBasicRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return securedBasicRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
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
