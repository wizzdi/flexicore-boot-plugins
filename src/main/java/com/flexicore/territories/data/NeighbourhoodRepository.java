package com.flexicore.territories.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Neighbourhood_;
import com.flexicore.model.territories.Street;
import com.flexicore.model.territories.Street_;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.NeighbourhoodFiltering;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@Extension
@Component
public class NeighbourhoodRepository extends AbstractRepositoryPlugin {

	public List<Neighbourhood> getAllNeighbourhoods(
			SecurityContext securityContext, NeighbourhoodFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Neighbourhood> q = cb.createQuery(Neighbourhood.class);
		Root<Neighbourhood> r = q.from(Neighbourhood.class);
		List<Predicate> preds = new ArrayList<>();
		addNeighbourhoodPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Neighbourhood> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Neighbourhood.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	public long countAllNeighbourhoods(SecurityContext securityContext,
			NeighbourhoodFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Neighbourhood> r = q.from(Neighbourhood.class);
		List<Predicate> preds = new ArrayList<>();
		addNeighbourhoodPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Neighbourhood> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Neighbourhood.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	private void addNeighbourhoodPredicate(NeighbourhoodFiltering filtering,
			CriteriaBuilder cb, Root<Neighbourhood> r, List<Predicate> preds) {
		if (filtering.getExternalIds() != null
				&& !filtering.getExternalIds().isEmpty()) {
			preds.add(r.get(Neighbourhood_.externalId).in(
					filtering.getExternalIds()));
		}

	}
}