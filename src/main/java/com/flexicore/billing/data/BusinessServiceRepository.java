package com.flexicore.billing.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.request.BusinessServiceFiltering;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.organization.interfaces.ISiteRepository;
import com.flexicore.billing.model.BusinessService;
import com.flexicore.billing.model.BusinessService_;
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
public class BusinessServiceRepository extends AbstractRepositoryPlugin {

	public List<BusinessService> getAllBusinessServices(SecurityContext securityContext,
                                       BusinessServiceFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BusinessService> q = cb.createQuery(BusinessService.class);
		Root<BusinessService> r = q.from(BusinessService.class);
		List<Predicate> preds = new ArrayList<>();
		addBusinessServicePredicates(filtering, cb, r, preds);
		QueryInformationHolder<BusinessService> queryInformationHolder = new QueryInformationHolder<>(
				filtering, BusinessService.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public long countAllBusinessServices(SecurityContext securityContext,
			BusinessServiceFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<BusinessService> r = q.from(BusinessService.class);
		List<Predicate> preds = new ArrayList<>();
		addBusinessServicePredicates(filtering, cb, r, preds);
		QueryInformationHolder<BusinessService> queryInformationHolder = new QueryInformationHolder<>(
				filtering, BusinessService.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addBusinessServicePredicates(BusinessServiceFiltering filtering,
                                     CriteriaBuilder cb, Root<BusinessService> r, List<Predicate> preds) {

	}

}