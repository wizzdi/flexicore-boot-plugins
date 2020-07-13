package com.flexicore.billing.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.*;
import com.flexicore.billing.request.PriceListToServiceFiltering;
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
public class PriceListToServiceRepository extends AbstractRepositoryPlugin {

	public List<PriceListToService> getAllPriceListToServices(SecurityContext securityContext,
                                       PriceListToServiceFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PriceListToService> q = cb.createQuery(PriceListToService.class);
		Root<PriceListToService> r = q.from(PriceListToService.class);
		List<Predicate> preds = new ArrayList<>();
		addPriceListToServicePredicates(filtering, cb, r, preds);
		QueryInformationHolder<PriceListToService> queryInformationHolder = new QueryInformationHolder<>(
				filtering, PriceListToService.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public long countAllPriceListToServices(SecurityContext securityContext,
			PriceListToServiceFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<PriceListToService> r = q.from(PriceListToService.class);
		List<Predicate> preds = new ArrayList<>();
		addPriceListToServicePredicates(filtering, cb, r, preds);
		QueryInformationHolder<PriceListToService> queryInformationHolder = new QueryInformationHolder<>(
				filtering, PriceListToService.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addPriceListToServicePredicates(PriceListToServiceFiltering filtering,
                                     CriteriaBuilder cb, Root<PriceListToService> r, List<Predicate> preds) {
		if (filtering.getBusinessServices() != null && !filtering.getBusinessServices().isEmpty()) {
			Set<String> ids = filtering.getBusinessServices().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<PriceListToService, BusinessService> join = r.join(PriceListToService_.businessService);
			preds.add(join.get(BusinessService_.id).in(ids));
		}

		if (filtering.getCurrencies() != null && !filtering.getCurrencies().isEmpty()) {
			Set<String> ids = filtering.getCurrencies().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<PriceListToService, Currency> join = r.join(PriceListToService_.currency);
			preds.add(join.get(Currency_.id).in(ids));
		}

		if (filtering.getPriceLists() != null && !filtering.getPriceLists().isEmpty()) {
			Set<String> ids = filtering.getPriceLists().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<PriceListToService, PriceList> join = r.join(PriceListToService_.priceList);
			preds.add(join.get(PriceList_.id).in(ids));
		}

	}

}