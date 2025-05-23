package com.flexicore.license.data;

import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.request.LicensingFeatureFiltering;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
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
public class LicensingFeatureRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private LicensingEntityRepository licensingEntityRepository;


	public List<LicensingFeature> listAllLicensingFeatures(LicensingFeatureFiltering licensingFeatureFiltering, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LicensingFeature> q = cb.createQuery(LicensingFeature.class);
		Root<LicensingFeature> r = q.from(LicensingFeature.class);
		List<Predicate> preds = new ArrayList<>();
		addLicensingFeaturesPredicates(licensingFeatureFiltering, cb,q,r, preds,securityContext);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<LicensingFeature> query = em.createQuery(q);
		BasicRepository.addPagination(licensingFeatureFiltering, query);
		return query.getResultList();
	}

	public <T extends LicensingFeature> void addLicensingFeaturesPredicates(LicensingFeatureFiltering licensingFeatureFiltering,CriteriaBuilder cb,CommonAbstractCriteria q, From<?,T> r,  List<Predicate> preds,SecurityContext securityContext) {
		licensingEntityRepository.addLicensingEntitiesPredicates(licensingFeatureFiltering,cb,q, r, preds,securityContext);
	}

	public long countAllLicensingFeatures(LicensingFeatureFiltering licensingFeatureFiltering, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<LicensingFeature> r = q.from(LicensingFeature.class);
		List<Predicate> preds = new ArrayList<>();
		addLicensingFeaturesPredicates(licensingFeatureFiltering, cb,q,r, preds,securityContext);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return licensingEntityRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return licensingEntityRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return licensingEntityRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return licensingEntityRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return licensingEntityRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return licensingEntityRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return licensingEntityRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		licensingEntityRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		licensingEntityRepository.massMerge(toMerge);
	}
}
