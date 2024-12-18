package com.flexicore.license.data;

import com.flexicore.license.model.*;
import com.flexicore.license.request.LicenseRequestFiltering;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
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
import java.util.stream.Collectors;

@Component

@Extension
public class LicenseRequestRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private SecuredBasicRepository securedBasicRepository;


	public List<LicenseRequest> listAllLicenseRequests(LicenseRequestFiltering licenseRequestFiltering, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LicenseRequest> q = cb.createQuery(LicenseRequest.class);
		Root<LicenseRequest> r = q.from(LicenseRequest.class);
		List<Predicate> preds = new ArrayList<>();
		addLicenseRequestsPredicates(licenseRequestFiltering,cb,q,r, preds,securityContext);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<LicenseRequest> query = em.createQuery(q);
		BasicRepository.addPagination(licenseRequestFiltering, query);
		return query.getResultList();
	}

	public <T extends LicenseRequest> void addLicenseRequestsPredicates(LicenseRequestFiltering licenseRequestFiltering,  CriteriaBuilder cb,CommonAbstractCriteria q,From<?,T> r, List<Predicate> preds,SecurityContext securityContext) {
		securedBasicRepository.addSecuredBasicPredicates(licenseRequestFiltering.getBasicPropertiesFilter(),cb,q,r,preds,securityContext);
		Join<T, LicenseRequestToEntity> join = null;
		if (licenseRequestFiltering.getLicensingFeatures() != null && !licenseRequestFiltering.getLicensingFeatures().isEmpty()) {
			Set<String> ids = licenseRequestFiltering.getLicensingFeatures().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			join = join == null ? r.join(LicenseRequest_.requestToEntity) : join;
			Join<LicenseRequestToEntity, LicensingFeature> join1 = cb.treat(join.join(LicenseRequestToEntity_.licensingEntity), LicensingFeature.class);
			preds.add(join1.get(LicensingFeature_.id).in(ids));
		}


		if (licenseRequestFiltering.getLicensingProducts() != null && !licenseRequestFiltering.getLicensingProducts().isEmpty()) {
			Set<String> ids = licenseRequestFiltering.getLicensingProducts().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			join = join == null ? r.join(LicenseRequest_.requestToEntity) : join;
			Join<LicenseRequestToEntity, LicensingProduct> join1 = cb.treat(join.join(LicenseRequestToEntity_.licensingEntity), LicensingProduct.class);
			preds.add(join1.get(LicensingProduct_.id).in(ids));
		}
		if (licenseRequestFiltering.getSigned() != null) {
			preds.add(licenseRequestFiltering.getSigned() ? r.get(LicenseRequest_.license).isNotNull() : r.get(LicenseRequest_.license).isNull());
		}
		if (licenseRequestFiltering.getExpirationDateAfter() != null) {
			join = join == null ? r.join(LicenseRequest_.requestToEntity) : join;
			preds.add(cb.greaterThan(join.get(LicenseRequestToEntity_.expiration), licenseRequestFiltering.getExpirationDateAfter()));
		}

	}

	public long countAllLicenseRequests(LicenseRequestFiltering licenseRequestFiltering, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<LicenseRequest> r = q.from(LicenseRequest.class);
		List<Predicate> preds = new ArrayList<>();
		addLicenseRequestsPredicates(licenseRequestFiltering, cb,q,r, preds,securityContext);
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
