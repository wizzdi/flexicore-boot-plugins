package com.flexicore.license.data;

import com.flexicore.license.model.LicensingEntity;
import com.flexicore.license.model.LicensingEntity_;
import com.flexicore.license.request.LicensingEntityFiltering;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component

@Extension
public class LicensingEntityRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private SecuredBasicRepository securedBasicRepository;


	public List<LicensingEntity> listAllLicensingEntities(LicensingEntityFiltering licensingEntityFiltering, SecurityContextBase securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LicensingEntity> q = cb.createQuery(LicensingEntity.class);
		Root<LicensingEntity> r = q.from(LicensingEntity.class);
		List<Predicate> preds = new ArrayList<>();
		addLicensingEntitiesPredicates(licensingEntityFiltering, cb,q,r, preds,securityContext);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<LicensingEntity> query = em.createQuery(q);
		BasicRepository.addPagination(licensingEntityFiltering, query);
		return query.getResultList();
	}

	public <T extends LicensingEntity> void addLicensingEntitiesPredicates(LicensingEntityFiltering licensingEntityFiltering,CriteriaBuilder cb,CommonAbstractCriteria q, From<?,T> r,  List<Predicate> preds,SecurityContextBase securityContext) {
	    securedBasicRepository.addSecuredBasicPredicates(licensingEntityFiltering.getBasicPropertiesFilter(), cb,q,r,preds,securityContext);
		if (licensingEntityFiltering.getCanonicalNames() != null && !licensingEntityFiltering.getCanonicalNames().isEmpty()) {
			preds.add(r.get(LicensingEntity_.canonicalName).in(licensingEntityFiltering.getCanonicalNames()));
		}
	}

	public long countAllLicensingEntities(LicensingEntityFiltering licensingEntityFiltering, SecurityContextBase securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<LicensingEntity> r = q.from(LicensingEntity.class);
		List<Predicate> preds = new ArrayList<>();
		addLicensingEntitiesPredicates(licensingEntityFiltering,cb,q,r, preds,securityContext);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();
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