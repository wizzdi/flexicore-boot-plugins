package com.flexicore.license.data;

import com.flexicore.license.model.LicenseRequestToFeature;
import com.flexicore.license.request.LicenseRequestToFeatureFiltering;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
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

@Component

@Extension
public class LicenseRequestToFeatureRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private LicenseRequestToEntityRepository licenseRequestToEntityRepository;


	public List<LicenseRequestToFeature> listAllLicenseRequestToFeatures(LicenseRequestToFeatureFiltering licenseRequestToFeatureFiltering, SecurityContextBase securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LicenseRequestToFeature> q = cb.createQuery(LicenseRequestToFeature.class);
		Root<LicenseRequestToFeature> r = q.from(LicenseRequestToFeature.class);
		List<Predicate> preds = new ArrayList<>();
		addLicenseRequestToFeaturesPredicates(licenseRequestToFeatureFiltering, cb, q, r, preds, securityContext);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<LicenseRequestToFeature> query = em.createQuery(q);
		BasicRepository.addPagination(licenseRequestToFeatureFiltering, query);
		return query.getResultList();
	}

	public <T extends LicenseRequestToFeature> void addLicenseRequestToFeaturesPredicates(LicenseRequestToFeatureFiltering licenseRequestToFeatureFiltering, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> preds, SecurityContextBase securityContext) {
		licenseRequestToEntityRepository.addLicenseRequestToEntitiesPredicates(licenseRequestToFeatureFiltering, cb, q, r, preds, securityContext);
	}

	public long countAllLicenseRequestToFeatures(LicenseRequestToFeatureFiltering licenseRequestToFeatureFiltering, SecurityContextBase securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<LicenseRequestToFeature> r = q.from(LicenseRequestToFeature.class);
		List<Predicate> preds = new ArrayList<>();
		addLicenseRequestToFeaturesPredicates(licenseRequestToFeatureFiltering, cb, q, r, preds, securityContext);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return licenseRequestToEntityRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return licenseRequestToEntityRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return licenseRequestToEntityRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return licenseRequestToEntityRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return licenseRequestToEntityRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return licenseRequestToEntityRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return licenseRequestToEntityRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		licenseRequestToEntityRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		licenseRequestToEntityRepository.massMerge(toMerge);
	}
}