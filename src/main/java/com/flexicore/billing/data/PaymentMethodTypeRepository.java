package com.flexicore.billing.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.PaymentMethodType;
import com.flexicore.billing.model.PaymentMethodType_;
import com.flexicore.billing.request.PaymentMethodTypeFiltering;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@PluginInfo(version = 1)
@Extension
@Component
public class PaymentMethodTypeRepository extends AbstractRepositoryPlugin {

    public List<PaymentMethodType> getAllPaymentMethodTypes(SecurityContext securityContext,
                                                            PaymentMethodTypeFiltering filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PaymentMethodType> q = cb.createQuery(PaymentMethodType.class);
        Root<PaymentMethodType> r = q.from(PaymentMethodType.class);
        List<Predicate> preds = new ArrayList<>();
        addPaymentMethodTypePredicates(filtering, cb, r, preds);
        QueryInformationHolder<PaymentMethodType> queryInformationHolder = new QueryInformationHolder<>(
                filtering, PaymentMethodType.class, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);
    }

    public long countAllPaymentMethodTypes(SecurityContext securityContext,
                                           PaymentMethodTypeFiltering filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<PaymentMethodType> r = q.from(PaymentMethodType.class);
        List<Predicate> preds = new ArrayList<>();
        addPaymentMethodTypePredicates(filtering, cb, r, preds);
        QueryInformationHolder<PaymentMethodType> queryInformationHolder = new QueryInformationHolder<>(
                filtering, PaymentMethodType.class, securityContext);
        return countAllFiltered(queryInformationHolder, preds, cb, q, r);
    }

    private void addPaymentMethodTypePredicates(PaymentMethodTypeFiltering filtering,
                                                CriteriaBuilder cb, Root<PaymentMethodType> r, List<Predicate> preds) {
        if (filtering.getCanonicalClassNames() != null && !filtering.getCanonicalClassNames().isEmpty()) {
            preds.add(r.get(PaymentMethodType_.canonicalClassName).in(filtering.getCanonicalClassNames()));
        }

    }

}