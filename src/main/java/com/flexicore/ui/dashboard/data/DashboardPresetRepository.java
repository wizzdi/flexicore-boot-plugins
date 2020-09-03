package com.flexicore.ui.dashboard.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.DashboardPreset;
import com.flexicore.ui.dashboard.model.DashboardPreset_;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.dashboard.model.GridLayout_;
import com.flexicore.ui.dashboard.request.DashboardPresetFiltering;
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
public class DashboardPresetRepository extends AbstractRepositoryPlugin {

	public List<DashboardPreset> listAllDashboardPreset(DashboardPresetFiltering dashboardPresetFiltering,
														SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardPreset> q = cb.createQuery(DashboardPreset.class);
		Root<DashboardPreset> r = q.from(DashboardPreset.class);
		List<Predicate> preds = new ArrayList<>();
		addDashboardPresetPredicates(preds, cb, r, dashboardPresetFiltering);
		QueryInformationHolder<DashboardPreset> queryInformationHolder = new QueryInformationHolder<>(
				dashboardPresetFiltering, DashboardPreset.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addDashboardPresetPredicates(List<Predicate> preds, CriteriaBuilder cb,
			Root<DashboardPreset> r, DashboardPresetFiltering dashboardPresetFiltering) {

		if(dashboardPresetFiltering.getGridLayouts()!=null &&!dashboardPresetFiltering.getGridLayouts().isEmpty()){
			Set<String> ids=dashboardPresetFiltering.getGridLayouts().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<DashboardPreset, GridLayout> join=r.join(DashboardPreset_.gridLayout);
			preds.add(join.get(GridLayout_.id).in(ids));
		}



	}

	public long countAllDashboardPreset(DashboardPresetFiltering dashboardPresetFiltering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<DashboardPreset> r = q.from(DashboardPreset.class);
		List<Predicate> preds = new ArrayList<>();
		addDashboardPresetPredicates(preds, cb, r, dashboardPresetFiltering);
		QueryInformationHolder<DashboardPreset> queryInformationHolder = new QueryInformationHolder<>(
				dashboardPresetFiltering, DashboardPreset.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}
}
