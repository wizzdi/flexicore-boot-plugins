package com.flexicore.organization.data;

import com.flexicore.model.*;
import com.flexicore.organization.model.Customer;
import com.flexicore.organization.model.Customer_;
import com.flexicore.organization.request.CustomerFiltering;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
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


@Extension
@Component
public class CustomerRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private SecuredBasicRepository securedBasicRepository;

	public <T extends Customer> void addCustomerPredicates(CustomerFiltering filtering,
														   CriteriaBuilder cb,CommonAbstractCriteria q, From<?,T> r, List<Predicate> preds,SecurityContextBase securityContextBase) {
		securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(), cb,q,r,preds,securityContextBase);
		if (filtering.getExternalIds() != null && !filtering.getExternalIds().isEmpty()) {
			preds.add(r.get(Customer_.externalId).in(filtering.getExternalIds()));
		}
		/*
		if (filtering.getUsers() != null && !filtering.getUsers().isEmpty()) {
			Set<String> ids = filtering.getUsers().stream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, SecurityTenant> tenantJoin = r.join(Customer_.tenant);
			Join<SecurityTenant, TenantToUser> tenantToUserJoin = tenantJoin.join(SecurityTenant_.tenantToUser);
			Join<TenantToUser, SecurityUser> join = cb.treat(tenantToUserJoin.join(Baselink_.rightside), SecurityUser.class);
			preds.add(join.get(SecurityUser_.id).in(ids));
		}*/

	}

	public List<Customer> getAllCustomers(SecurityContextBase securityContextBase,
										  CustomerFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Customer> q = cb.createQuery(Customer.class);
		Root<Customer> r = q.from(Customer.class);
		List<Predicate> preds = new ArrayList<>();
		addCustomerPredicates(filtering, cb,q, r, preds,securityContextBase);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<Customer> query = em.createQuery(q);
		BasicRepository.addPagination(filtering, query);
		return query.getResultList();
	}

	public long countAllCustomers(SecurityContextBase securityContextBase,
								  CustomerFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Customer> r = q.from(Customer.class);
		List<Predicate> preds = new ArrayList<>();
		addCustomerPredicates(filtering, cb,q, r, preds,securityContextBase);
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