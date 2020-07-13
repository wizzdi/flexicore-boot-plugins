package com.flexicore.billing.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.Currency;
import com.flexicore.billing.request.CurrencyFiltering;
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
public class CurrencyRepository extends AbstractRepositoryPlugin {

	public List<Currency> getAllCurrencies(SecurityContext securityContext,
                                       CurrencyFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Currency> q = cb.createQuery(Currency.class);
		Root<Currency> r = q.from(Currency.class);
		List<Predicate> preds = new ArrayList<>();
		addCurrencyPredicates(filtering, cb, r, preds);
		QueryInformationHolder<Currency> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Currency.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public long countAllCurrencies(SecurityContext securityContext,
			CurrencyFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Currency> r = q.from(Currency.class);
		List<Predicate> preds = new ArrayList<>();
		addCurrencyPredicates(filtering, cb, r, preds);
		QueryInformationHolder<Currency> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Currency.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addCurrencyPredicates(CurrencyFiltering filtering,
                                     CriteriaBuilder cb, Root<Currency> r, List<Predicate> preds) {

	}

}