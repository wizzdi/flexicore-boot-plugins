package com.flexicore.billing.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.*;
import com.flexicore.billing.request.PaymentMethodFiltering;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.organization.model.Customer;
import com.flexicore.organization.model.Customer_;
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
public class PaymentMethodRepository extends AbstractRepositoryPlugin {

	public List<PaymentMethod> getAllPaymentMethods(SecurityContext securityContext,
                                       PaymentMethodFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PaymentMethod> q = cb.createQuery(PaymentMethod.class);
		Root<PaymentMethod> r = q.from(PaymentMethod.class);
		List<Predicate> preds = new ArrayList<>();
		addPaymentMethodPredicates(filtering, cb, r, preds);
		QueryInformationHolder<PaymentMethod> queryInformationHolder = new QueryInformationHolder<>(
				filtering, PaymentMethod.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public long countAllPaymentMethods(SecurityContext securityContext,
			PaymentMethodFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<PaymentMethod> r = q.from(PaymentMethod.class);
		List<Predicate> preds = new ArrayList<>();
		addPaymentMethodPredicates(filtering, cb, r, preds);
		QueryInformationHolder<PaymentMethod> queryInformationHolder = new QueryInformationHolder<>(
				filtering, PaymentMethod.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addPaymentMethodPredicates(PaymentMethodFiltering filtering,
                                     CriteriaBuilder cb, Root<PaymentMethod> r, List<Predicate> preds) {
		if (filtering.getCustomers() != null && !filtering.getCustomers().isEmpty()) {
			Set<String> ids = filtering.getCustomers().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<PaymentMethod, Customer> join = r.join(PaymentMethod_.customer);
			preds.add(join.get(Customer_.id).in(ids));
		}
		if (filtering.getPaymentMethodTypes() != null && !filtering.getPaymentMethodTypes().isEmpty()) {
			Set<String> ids = filtering.getPaymentMethodTypes().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<PaymentMethod, PaymentMethodType> join = r.join(PaymentMethod_.paymentMethodType);
			preds.add(join.get(PaymentMethodType_.id).in(ids));
		}
		if(filtering.getActive()!=null){
			preds.add(cb.equal(r.get(PaymentMethod_.active),filtering.getActive()));
		}

	}

}