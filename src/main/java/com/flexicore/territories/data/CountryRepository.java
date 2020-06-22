package com.flexicore.territories.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.Country;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.CountryFiltering;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@Extension
@Component
public class CountryRepository extends AbstractRepositoryPlugin {

	public List<Country> listAllCountries(SecurityContext securityContext,
			CountryFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Country> q = cb.createQuery(Country.class);
		Root<Country> r = q.from(Country.class);
		List<Predicate> preds = new ArrayList<>();
		addCountryPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Country> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Country.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	public long countAllCountries(SecurityContext securityContext,
			CountryFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Country> r = q.from(Country.class);
		List<Predicate> preds = new ArrayList<>();
		addCountryPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Country> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Country.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	private void addCountryPredicate(CountryFiltering filtering,
			CriteriaBuilder cb, Root<Country> r, List<Predicate> preds) {

	}
}