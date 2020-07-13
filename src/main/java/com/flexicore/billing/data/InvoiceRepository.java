package com.flexicore.billing.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.*;
import com.flexicore.billing.request.InvoiceFiltering;
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
public class InvoiceRepository extends AbstractRepositoryPlugin {

	public List<Invoice> getAllInvoices(SecurityContext securityContext,
                                       InvoiceFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Invoice> q = cb.createQuery(Invoice.class);
		Root<Invoice> r = q.from(Invoice.class);
		List<Predicate> preds = new ArrayList<>();
		addInvoicePredicates(filtering, cb, r, preds);
		QueryInformationHolder<Invoice> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Invoice.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public long countAllInvoices(SecurityContext securityContext,
			InvoiceFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Invoice> r = q.from(Invoice.class);
		List<Predicate> preds = new ArrayList<>();
		addInvoicePredicates(filtering, cb, r, preds);
		QueryInformationHolder<Invoice> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Invoice.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addInvoicePredicates(InvoiceFiltering filtering, CriteriaBuilder cb, Root<Invoice> r, List<Predicate> preds) {

		if (filtering.getPaymentMethods() != null && !filtering.getPaymentMethods().isEmpty()) {
			Set<String> ids = filtering.getPaymentMethods().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<Invoice, PaymentMethod> join = r.join(Invoice_.usedPaymentMethod);
			preds.add(join.get(PaymentMethod_.id).in(ids));
		}

	}

}