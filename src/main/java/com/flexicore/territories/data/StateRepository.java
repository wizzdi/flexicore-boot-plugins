package com.flexicore.territories.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.*;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.StateFiltering;

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
public class StateRepository extends AbstractRepositoryPlugin {

	public List<State> listAllStates(SecurityContext securityContext,
			StateFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<State> q = cb.createQuery(State.class);
		Root<State> r = q.from(State.class);
		List<Predicate> preds = new ArrayList<>();
		addStatePredicate(filtering, cb, r, preds);
		QueryInformationHolder<State> queryInformationHolder = new QueryInformationHolder<>(
				filtering, State.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	public long countAllStates(SecurityContext securityContext,
			StateFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<State> r = q.from(State.class);
		List<Predicate> preds = new ArrayList<>();
		addStatePredicate(filtering, cb, r, preds);
		QueryInformationHolder<State> queryInformationHolder = new QueryInformationHolder<>(
				filtering, State.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	private void addStatePredicate(StateFiltering filtering,
			CriteriaBuilder cb, Root<State> r, List<Predicate> preds) {
		if (filtering.getCountries() != null && !filtering.getCountries().isEmpty()) {
			Set<String> ids=filtering.getCountries().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<State, Country> countryJoin=r.join(State_.country);
			preds.add(countryJoin.get(Country_.id).in(ids));
		}

	}
}