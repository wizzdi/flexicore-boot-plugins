package com.flexicore.ui.dashboard.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.*;
import com.flexicore.ui.dashboard.request.GridLayoutCellFiltering;
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
public class GridLayoutCellRepository extends AbstractRepositoryPlugin {

	public List<GridLayoutCell> listAllGridLayoutCell(GridLayoutCellFiltering gridLayoutCellFiltering,
														SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<GridLayoutCell> q = cb.createQuery(GridLayoutCell.class);
		Root<GridLayoutCell> r = q.from(GridLayoutCell.class);
		List<Predicate> preds = new ArrayList<>();
		addGridLayoutCellPredicates(preds, cb, r, gridLayoutCellFiltering);
		QueryInformationHolder<GridLayoutCell> queryInformationHolder = new QueryInformationHolder<>(
				gridLayoutCellFiltering, GridLayoutCell.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addGridLayoutCellPredicates(List<Predicate> preds, CriteriaBuilder cb,
			Root<GridLayoutCell> r, GridLayoutCellFiltering gridLayoutCellFiltering) {


		if(gridLayoutCellFiltering.getGridLayouts()!=null &&!gridLayoutCellFiltering.getGridLayouts().isEmpty()){
			Set<String> ids=gridLayoutCellFiltering.getGridLayouts().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<GridLayoutCell, GridLayout> join=r.join(GridLayoutCell_.gridLayout);
			preds.add(join.get(GridLayout_.id).in(ids));
		}

	}

	public long countAllGridLayoutCell(GridLayoutCellFiltering gridLayoutCellFiltering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<GridLayoutCell> r = q.from(GridLayoutCell.class);
		List<Predicate> preds = new ArrayList<>();
		addGridLayoutCellPredicates(preds, cb, r, gridLayoutCellFiltering);
		QueryInformationHolder<GridLayoutCell> queryInformationHolder = new QueryInformationHolder<>(
				gridLayoutCellFiltering, GridLayoutCell.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}
}
