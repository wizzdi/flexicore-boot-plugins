package com.flexicore.territories.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.*;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.CityFiltering;

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
public class CityRepository extends AbstractRepositoryPlugin {

	public List<City> listAllCities(SecurityContext securityContext,
			CityFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<City> q = cb.createQuery(City.class);
		Root<City> r = q.from(City.class);
		List<Predicate> preds = new ArrayList<>();
		addCityPredicate(filtering, cb, r, preds);
		QueryInformationHolder<City> queryInformationHolder = new QueryInformationHolder<>(
				filtering, City.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	public long countAllCities(SecurityContext securityContext,
			CityFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<City> r = q.from(City.class);
		List<Predicate> preds = new ArrayList<>();
		addCityPredicate(filtering, cb, r, preds);
		QueryInformationHolder<City> queryInformationHolder = new QueryInformationHolder<>(
				filtering, City.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	private void addCityPredicate(CityFiltering filtering, CriteriaBuilder cb,
			Root<City> r, List<Predicate> preds) {

		if (filtering.getCountries() != null && !filtering.getCountries().isEmpty()) {
			Set<String> ids=filtering.getCountries().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<City, Country> countryJoin=r.join(City_.country);
			preds.add(countryJoin.get(Country_.id).in(ids));
		}

		if (filtering.getStates() != null && !filtering.getStates().isEmpty()) {
			Set<String> ids=filtering.getStates().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<City, State> stateJoin=r.join(City_.state);
			preds.add(stateJoin.get(State_.id).in(ids));
		}

	}
}