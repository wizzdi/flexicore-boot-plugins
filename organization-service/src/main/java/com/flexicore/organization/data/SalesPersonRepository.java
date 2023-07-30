package com.flexicore.organization.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.organization.model.*;
import com.flexicore.organization.request.SalesPersonFiltering;
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
public class SalesPersonRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private EmployeeRepository employeeRepository;

	public List<SalesPerson> listAllSalesPeople(
			SecurityContextBase securityContext, SalesPersonFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SalesPerson> q = cb.createQuery(SalesPerson.class);
		Root<SalesPerson> r = q.from(SalesPerson.class);
		List<Predicate> preds = new ArrayList<>();
		addSalesPersonPredicates(filtering, cb,q, r, preds,securityContext);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<SalesPerson> query = em.createQuery(q);
		BasicRepository.addPagination(filtering, query);
		return query.getResultList();
	}

	public <T extends SalesPerson> void addSalesPersonPredicates(SalesPersonFiltering filtering,
										  CriteriaBuilder cb,CommonAbstractCriteria q, From<?,T> r, List<Predicate> preds,SecurityContextBase securityContext) {
		employeeRepository.addEmployeePredicates(filtering, cb,q, r, preds,securityContext);
		if (filtering.getSalesRegions() != null && !filtering.getSalesRegions().isEmpty()) {
			Set<String> ids = filtering.getSalesRegions().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, SalesPersonToRegion> j1 = r.join(SalesPerson_.salesPersonToRegions);
			Join<SalesPersonToRegion, SalesRegion> join = cb.treat(j1.join(SalesPersonToRegion_.salesRegion), SalesRegion.class);
			preds.add(join.get(SalesPerson_.id).in(ids));
		}
	}

	public Long countAllSalesPeople(SecurityContextBase securityContext,
									SalesPersonFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<SalesPerson> r = q.from(SalesPerson.class);
		List<Predicate> preds = new ArrayList<>();
		addSalesPersonPredicates(filtering, cb,q, r, preds,securityContext);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();
	}



	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return employeeRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return employeeRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return employeeRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return employeeRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return employeeRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return employeeRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return employeeRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		employeeRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		employeeRepository.massMerge(toMerge);
	}
}