package com.flexicore.organization.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.organization.model.*;
import com.flexicore.organization.request.OrganizationFiltering;
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


@Extension
@Component
public class OrganizationRepository implements Plugin {

	@Autowired
	private SecuredBasicRepository securedBasicRepository;
	@PersistenceContext
	private EntityManager em;


	public <T extends Organization> void addOrganizationPredicates(OrganizationFiltering filtering, CriteriaBuilder cb, CommonAbstractCriteria q, From<?,T> r, List<Predicate> preds, SecurityContext securityContext) {
		securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(),cb,q,r,preds,securityContext);
		if (filtering.getExternalIds()!=null &&!filtering.getExternalIds().isEmpty()){
			preds.add(r.get(Organization_.externalId).in(filtering.getExternalIds()));
		}

	}

	public List<Organization> getAllOrganizations(SecurityContext securityContext,
										   OrganizationFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Organization> q = cb.createQuery(Organization.class);
		Root<Organization> r = q.from(Organization.class);
		List<Predicate> preds = new ArrayList<>();
		addOrganizationPredicates(filtering, cb,q, r, preds,securityContext);
		q.select(r).where(preds.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(Organization_.name)));
		TypedQuery<Organization> query = em.createQuery(q);
		BasicRepository.addPagination(filtering, query);
		return query.getResultList();
	}

	public long countAllOrganizations(SecurityContext securityContext,
								   OrganizationFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Organization> r = q.from(Organization.class);
		List<Predicate> preds = new ArrayList<>();
		addOrganizationPredicates(filtering, cb,q, r, preds,securityContext);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();
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
