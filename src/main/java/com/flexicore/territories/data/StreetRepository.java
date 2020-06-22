package com.flexicore.territories.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.Street;
import com.flexicore.model.territories.Street_;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.StreetFiltering;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@Extension
@Component
public class StreetRepository extends AbstractRepositoryPlugin {

	public List<Street> listAllStreets(SecurityContext securityContext,
			StreetFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Street> q = cb.createQuery(Street.class);
		Root<Street> r = q.from(Street.class);
		List<Predicate> preds = new ArrayList<>();
		addStreetPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Street> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Street.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	public long countAllStreets(SecurityContext securityContext,
			StreetFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Street> r = q.from(Street.class);
		List<Predicate> preds = new ArrayList<>();
		addStreetPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Street> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Street.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	private void addStreetPredicate(StreetFiltering filtering,
			CriteriaBuilder cb, Root<Street> r, List<Predicate> preds) {
		if (filtering.getCity() != null) {
			preds.add(cb.equal(r.get(Street_.city), filtering.getCity()));
		}

		if (filtering.getExternalIds() != null
				&& !filtering.getExternalIds().isEmpty()) {
			preds.add(r.get(Street_.externalId).in(filtering.getExternalIds()));
		}

	}
}