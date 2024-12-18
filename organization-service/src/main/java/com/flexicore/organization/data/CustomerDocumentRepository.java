package com.flexicore.organization.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.organization.model.Customer;
import com.flexicore.organization.model.CustomerDocument;
import com.flexicore.organization.model.CustomerDocument_;
import com.flexicore.organization.model.Customer_;
import com.flexicore.organization.request.CustomerDocumentFiltering;
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
public class CustomerDocumentRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private SecuredBasicRepository securedBasicRepository;

	public List<CustomerDocument> getAllCustomerDocuments(SecurityContext securityContext,
														  CustomerDocumentFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomerDocument> q = cb.createQuery(CustomerDocument.class);
		Root<CustomerDocument> r = q.from(CustomerDocument.class);
		List<Predicate> preds = new ArrayList<>();
		addCustomerDocumentPredicates(filtering, cb,q, r, preds,securityContext);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<CustomerDocument> query = em.createQuery(q);
		BasicRepository.addPagination(filtering, query);
		return query.getResultList();
	}

	public long countAllCustomerDocuments(SecurityContext securityContext,
										  CustomerDocumentFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<CustomerDocument> r = q.from(CustomerDocument.class);
		List<Predicate> preds = new ArrayList<>();
		addCustomerDocumentPredicates(filtering, cb,q, r, preds,securityContext);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();
	}

	private void addCustomerDocumentPredicates(CustomerDocumentFiltering filtering, CriteriaBuilder cb,CommonAbstractCriteria q, Root<CustomerDocument> r, List<Predicate> preds,SecurityContext securityContext) {
		securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(), cb,q,r,preds,securityContext);
		if (filtering.getCustomers() != null && !filtering.getCustomers().isEmpty()) {
			Set<String> ids = filtering.getCustomers().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<CustomerDocument, Customer> join = r.join(CustomerDocument_.customer);
			preds.add(join.get(Customer_.id).in(ids));
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
