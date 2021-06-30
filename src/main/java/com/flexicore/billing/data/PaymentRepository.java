package com.flexicore.billing.data;


import com.flexicore.billing.model.*;
import com.flexicore.billing.request.PaymentFiltering;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
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
public class PaymentRepository implements Plugin{
@PersistenceContext
private EntityManager em;
@Autowired
private SecuredBasicRepository securedBasicRepository;

	public List<Payment> getAllPayments(SecurityContextBase securityContext,
                                       PaymentFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Payment> q = cb.createQuery(Payment.class);
		Root<Payment> r = q.from(Payment.class);
		List<Predicate> preds = new ArrayList<>();
		addPaymentPredicates(filtering, cb,q, r, preds,securityContext);
q.select(r).where(preds.toArray(Predicate[]::new));
TypedQuery<Payment> query=em.createQuery(q);
BasicRepository.addPagination(filtering,query);
return query.getResultList();
	}

	public long countAllPayments(SecurityContextBase securityContext,
			PaymentFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Payment> r = q.from(Payment.class);
		List<Predicate> preds = new ArrayList<>();
		addPaymentPredicates(filtering, cb,q, r, preds,securityContext);
q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
TypedQuery<Long> query=em.createQuery(q);
return query.getSingleResult();
	}

	public <T extends Payment> void  addPaymentPredicates(PaymentFiltering filtering,
                                     CriteriaBuilder cb, CommonAbstractCriteria q,  From<?,T> r, List<Predicate> preds, SecurityContextBase securityContext) {
		securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(), cb,q,r,preds,securityContext);


		if (filtering.getPaymentReferenceLike() != null ) {

			preds.add(cb.like(r.get(Payment_.paymentReference),filtering.getPaymentReferenceLike()));
		}

		if (filtering.getInvoiceItems() != null && !filtering.getInvoiceItems().isEmpty()) {
			Set<String> ids = filtering.getInvoiceItems().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, InvoiceItem> join = r.join(Payment_.invoiceItems);
			preds.add(join.get(InvoiceItem_.id).in(ids));
		}

		if (filtering.getContractItems() != null && !filtering.getContractItems().isEmpty()) {
			Set<String> ids = filtering.getContractItems().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, ContractItem> join = r.join(Payment_.contractItem);
			preds.add(join.get(ContractItem_.id).in(ids));
		}

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