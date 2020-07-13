package com.flexicore.billing.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.*;
import com.flexicore.billing.request.InvoiceItemFiltering;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
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
public class InvoiceItemRepository extends AbstractRepositoryPlugin {

	public List<InvoiceItem> getAllInvoiceItems(SecurityContext securityContext,
                                       InvoiceItemFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<InvoiceItem> q = cb.createQuery(InvoiceItem.class);
		Root<InvoiceItem> r = q.from(InvoiceItem.class);
		List<Predicate> preds = new ArrayList<>();
		addInvoiceItemPredicates(filtering, cb, r, preds);
		QueryInformationHolder<InvoiceItem> queryInformationHolder = new QueryInformationHolder<>(
				filtering, InvoiceItem.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public long countAllInvoiceItems(SecurityContext securityContext,
			InvoiceItemFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<InvoiceItem> r = q.from(InvoiceItem.class);
		List<Predicate> preds = new ArrayList<>();
		addInvoiceItemPredicates(filtering, cb, r, preds);
		QueryInformationHolder<InvoiceItem> queryInformationHolder = new QueryInformationHolder<>(
				filtering, InvoiceItem.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addInvoiceItemPredicates(InvoiceItemFiltering filtering,
                                     CriteriaBuilder cb, Root<InvoiceItem> r, List<Predicate> preds) {

		if (filtering.getInvoices() != null && !filtering.getInvoices().isEmpty()) {
			Set<String> ids = filtering.getInvoices().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<InvoiceItem, Invoice> join = r.join(InvoiceItem_.invoice);
			preds.add(join.get(Invoice_.id).in(ids));
		}

		if (filtering.getContractItems() != null && !filtering.getContractItems().isEmpty()) {
			Set<String> ids = filtering.getContractItems().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<InvoiceItem, ContractItem> join = r.join(InvoiceItem_.contractItem);
			preds.add(join.get(ContractItem_.id).in(ids));
		}

	}

}