package com.flexicore.billing.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.*;
import com.flexicore.billing.request.ContractItemFiltering;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.organization.model.Branch;
import com.flexicore.organization.model.Branch_;
import com.flexicore.organization.model.Organization;
import com.flexicore.organization.model.Organization_;
import com.flexicore.security.SecurityContext;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
public class ContractItemRepository extends AbstractRepositoryPlugin {

	public List<ContractItem> getAllContractItems(SecurityContext securityContext, ContractItemFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ContractItem> q = cb.createQuery(ContractItem.class);
		Root<ContractItem> r = q.from(ContractItem.class);
		List<Predicate> preds = new ArrayList<>();
		addContractItemPredicates(filtering, cb, r, preds);
		QueryInformationHolder<ContractItem> queryInformationHolder = new QueryInformationHolder<>(
				filtering, ContractItem.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public long countAllContractItems(SecurityContext securityContext, ContractItemFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<ContractItem> r = q.from(ContractItem.class);
		List<Predicate> preds = new ArrayList<>();
		addContractItemPredicates(filtering, cb, r, preds);
		QueryInformationHolder<ContractItem> queryInformationHolder = new QueryInformationHolder<>(
				filtering, ContractItem.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addContractItemPredicates(ContractItemFiltering filtering, CriteriaBuilder cb, Root<ContractItem> r, List<Predicate> preds) {
		if (filtering.getBusinessServices() != null && !filtering.getBusinessServices().isEmpty()) {
			Set<String> ids = filtering.getBusinessServices().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<ContractItem, BusinessService> join = r.join(ContractItem_.businessService);
			preds.add(join.get(BusinessService_.id).in(ids));
		}

		if (filtering.getContracts() != null && !filtering.getContracts().isEmpty()) {
			Set<String> ids = filtering.getContracts().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<ContractItem, Contract> join = r.join(ContractItem_.contract);
			preds.add(join.get(Contract_.id).in(ids));
		}

		if (filtering.getPriceListToService() != null && !filtering.getPriceListToService().isEmpty()) {
			Set<String> ids = filtering.getPriceListToService().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<ContractItem, PriceListToService> join = r.join(ContractItem_.priceListToService);
			preds.add(join.get(PriceListToService_.id).in(ids));
		}

	}

}