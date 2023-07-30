package com.flexicore.organization.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.organization.model.Branch;
import com.flexicore.organization.model.Branch_;
import com.flexicore.organization.model.Organization;
import com.flexicore.organization.model.Organization_;
import com.flexicore.organization.request.BranchFiltering;
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
import java.util.stream.Collectors;


@Extension
@Component
public class BranchRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private SiteRepository siteRepository;


	public List<Branch> getAllBranches(SecurityContextBase securityContext,
									   BranchFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Branch> q = cb.createQuery(Branch.class);
		Root<Branch> r = q.from(Branch.class);
		List<Predicate> preds = new ArrayList<>();
		addBranchPredicates(filtering, cb,q, r, preds,securityContext);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<Branch> query = em.createQuery(q);
		BasicRepository.addPagination(filtering, query);
		return query.getResultList();
	}

	public long countAllBranches(SecurityContextBase securityContext,
								 BranchFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Branch> r = q.from(Branch.class);
		List<Predicate> preds = new ArrayList<>();
		addBranchPredicates(filtering, cb,q, r, preds,securityContext);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();
	}

	private void addBranchPredicates(BranchFiltering filtering,
									 CriteriaBuilder cb,CommonAbstractCriteria q, From<?,Branch> r, List<Predicate> preds,SecurityContextBase securityContext) {
		siteRepository.addSitePredicates(filtering, cb,q, r, preds,securityContext);
		if (filtering.getOrganizations() != null && !filtering.getOrganizations().isEmpty()) {
			Set<String> ids = filtering.getOrganizations().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<Branch, Organization> join = r.join(Branch_.organization);
			preds.add(join.get(Organization_.id).in(ids));
		}
	}



	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return siteRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return siteRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return siteRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return siteRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return siteRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return siteRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return siteRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		siteRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		siteRepository.massMerge(toMerge);
	}

}