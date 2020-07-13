package com.flexicore.billing.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.PriceList;
import com.flexicore.billing.request.PriceListFiltering;
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
public class PriceListRepository extends AbstractRepositoryPlugin {

	public List<PriceList> getAllPriceLists(SecurityContext securityContext,
                                       PriceListFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PriceList> q = cb.createQuery(PriceList.class);
		Root<PriceList> r = q.from(PriceList.class);
		List<Predicate> preds = new ArrayList<>();
		addPriceListPredicates(filtering, cb, r, preds);
		QueryInformationHolder<PriceList> queryInformationHolder = new QueryInformationHolder<>(
				filtering, PriceList.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public long countAllPriceLists(SecurityContext securityContext,
			PriceListFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<PriceList> r = q.from(PriceList.class);
		List<Predicate> preds = new ArrayList<>();
		addPriceListPredicates(filtering, cb, r, preds);
		QueryInformationHolder<PriceList> queryInformationHolder = new QueryInformationHolder<>(
				filtering, PriceList.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addPriceListPredicates(PriceListFiltering filtering,
                                     CriteriaBuilder cb, Root<PriceList> r, List<Predicate> preds) {

	}

}