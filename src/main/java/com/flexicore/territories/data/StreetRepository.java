package com.flexicore.territories.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.*;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.StreetFiltering;

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
		Join<Street, City> cityJoin=null;
		if (filtering.getCities() != null && !filtering.getCities().isEmpty()) {
			Set<String> ids=filtering.getCities().stream().map(f->f.getId()).collect(Collectors.toSet());
			cityJoin=cityJoin==null?r.join(Street_.city):cityJoin;
			preds.add(cityJoin.get(City_.id).in(ids));
		}
		if (filtering.getCountries() != null && !filtering.getCountries().isEmpty()) {
			Set<String> ids=filtering.getCountries().stream().map(f->f.getId()).collect(Collectors.toSet());
			cityJoin=cityJoin==null?r.join(Street_.city):cityJoin;
			Join<City, Country> countryJoin=cityJoin.join(City_.country);
			preds.add(countryJoin.get(Country_.id).in(ids));
		}
		if (filtering.getNeighbourhoods() != null && !filtering.getNeighbourhoods().isEmpty()) {
			Set<String> ids=filtering.getNeighbourhoods().stream().map(f->f.getId()).collect(Collectors.toSet());
			cityJoin=cityJoin==null?r.join(Street_.city):cityJoin;
			Join<City, Neighbourhood> neighbourhoodJoin=cityJoin.join(City_.neighbourhoods);
			preds.add(neighbourhoodJoin.get(Neighbourhood_.id).in(ids));
		}

		if (filtering.getExternalIds() != null && !filtering.getExternalIds().isEmpty()) {
			preds.add(r.get(Street_.externalId).in(filtering.getExternalIds()));
		}

	}
}