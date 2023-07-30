package com.flexicore.organization.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.organization.model.IndividualCustomer;
import com.flexicore.organization.request.IndividualCustomerFiltering;
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


@Extension
@Component
public class IndividualCustomerRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private CustomerRepository customerRepository;

	public <T extends IndividualCustomer> void addIndividualCustomerPredicates(IndividualCustomerFiltering filtering,
																					  CriteriaBuilder cb, CommonAbstractCriteria q, From<?,T> r, List<Predicate> preds,SecurityContextBase securityContext) {
		customerRepository.addCustomerPredicates(filtering, cb,q, r, preds,securityContext);

	}

	public List<IndividualCustomer> getAllIndividualCustomers(SecurityContextBase securityContext,
															  IndividualCustomerFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<IndividualCustomer> q = cb.createQuery(IndividualCustomer.class);
		Root<IndividualCustomer> r = q.from(IndividualCustomer.class);
		List<Predicate> preds = new ArrayList<>();
		addIndividualCustomerPredicates(filtering, cb,q, r, preds,securityContext);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<IndividualCustomer> query = em.createQuery(q);
		BasicRepository.addPagination(filtering, query);
		return query.getResultList();
	}

	public long countAllIndividualCustomers(SecurityContextBase securityContext,
											IndividualCustomerFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<IndividualCustomer> r = q.from(IndividualCustomer.class);
		List<Predicate> preds = new ArrayList<>();
		addIndividualCustomerPredicates(filtering, cb,q, r, preds,securityContext);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();
	}



	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return customerRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return customerRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return customerRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return customerRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return customerRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return customerRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return customerRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		customerRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		customerRepository.massMerge(toMerge);
	}
}