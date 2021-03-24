package com.flexicore.billing.data;


import com.flexicore.billing.model.*;
import com.flexicore.billing.request.ContractItemFiltering;
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
public class ContractItemRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private SecuredBasicRepository securedBasicRepository;

	public List<ContractItem> getAllContractItems(SecurityContextBase securityContext, ContractItemFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ContractItem> q = cb.createQuery(ContractItem.class);
		Root<ContractItem> r = q.from(ContractItem.class);
		List<Predicate> preds = new ArrayList<>();
		addContractItemPredicates(filtering, cb, q, r, preds, securityContext);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<ContractItem> query = em.createQuery(q);
		BasicRepository.addPagination(filtering, query);
		return query.getResultList();
	}

	public long countAllContractItems(SecurityContextBase securityContext, ContractItemFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<ContractItem> r = q.from(ContractItem.class);
		List<Predicate> preds = new ArrayList<>();
		addContractItemPredicates(filtering, cb, q, r, preds, securityContext);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();
	}

	public <T extends ContractItem> void addContractItemPredicates(ContractItemFiltering filtering, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> preds, SecurityContextBase securityContext) {

		securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(), cb,q,r,preds,securityContext);
		if (filtering.getBusinessServices() != null && !filtering.getBusinessServices().isEmpty()) {
			Set<String> ids = filtering.getBusinessServices().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, BusinessService> join = r.join(ContractItem_.businessService);
			preds.add(join.get(BusinessService_.id).in(ids));
		}

		if (filtering.getContracts() != null && !filtering.getContracts().isEmpty()) {
			Set<String> ids = filtering.getContracts().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, Contract> join = r.join(ContractItem_.contract);
			preds.add(join.get(Contract_.id).in(ids));
		}

		if (filtering.getPriceListToService() != null && !filtering.getPriceListToService().isEmpty()) {
			Set<String> ids = filtering.getPriceListToService().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, PriceListToService> join = r.join(ContractItem_.priceListToService);
			preds.add(join.get(PriceListToService_.id).in(ids));
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