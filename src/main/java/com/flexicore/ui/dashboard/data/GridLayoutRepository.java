package com.flexicore.ui.dashboard.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.dashboard.model.GridLayout_;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.dashboard.model.GridLayout_;
import com.flexicore.ui.dashboard.request.GridLayoutFiltering;
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
public class GridLayoutRepository extends AbstractRepositoryPlugin {

	public List<GridLayout> listAllGridLayout(GridLayoutFiltering gridLayoutFiltering,
														SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<GridLayout> q = cb.createQuery(GridLayout.class);
		Root<GridLayout> r = q.from(GridLayout.class);
		List<Predicate> preds = new ArrayList<>();
		addGridLayoutPredicates(preds, cb, r, gridLayoutFiltering);
		QueryInformationHolder<GridLayout> queryInformationHolder = new QueryInformationHolder<>(
				gridLayoutFiltering, GridLayout.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addGridLayoutPredicates(List<Predicate> preds, CriteriaBuilder cb,
			Root<GridLayout> r, GridLayoutFiltering gridLayoutFiltering) {




	}

	public long countAllGridLayout(GridLayoutFiltering gridLayoutFiltering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<GridLayout> r = q.from(GridLayout.class);
		List<Predicate> preds = new ArrayList<>();
		addGridLayoutPredicates(preds, cb, r, gridLayoutFiltering);
		QueryInformationHolder<GridLayout> queryInformationHolder = new QueryInformationHolder<>(
				gridLayoutFiltering, GridLayout.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}
}
