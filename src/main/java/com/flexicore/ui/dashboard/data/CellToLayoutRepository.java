package com.flexicore.ui.dashboard.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.model.dynamic.DynamicExecution_;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.*;
import com.flexicore.ui.dashboard.request.CellToLayoutFiltering;
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
public class CellToLayoutRepository extends AbstractRepositoryPlugin {

	public List<CellToLayout> listAllCellToLayout(CellToLayoutFiltering cellToLayoutFiltering,
														SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CellToLayout> q = cb.createQuery(CellToLayout.class);
		Root<CellToLayout> r = q.from(CellToLayout.class);
		List<Predicate> preds = new ArrayList<>();
		addCellToLayoutPredicates(preds, cb, r, cellToLayoutFiltering);
		QueryInformationHolder<CellToLayout> queryInformationHolder = new QueryInformationHolder<>(
				cellToLayoutFiltering, CellToLayout.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addCellToLayoutPredicates(List<Predicate> preds, CriteriaBuilder cb,
			Root<CellToLayout> r, CellToLayoutFiltering cellToLayoutFiltering) {

		if(cellToLayoutFiltering.getCellContents()!=null &&!cellToLayoutFiltering.getCellContents().isEmpty()){
			Set<String> ids=cellToLayoutFiltering.getCellContents().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<CellToLayout, CellContent> join=r.join(CellToLayout_.cellContent);
			preds.add(join.get(CellContent_.id).in(ids));
		}

		if(cellToLayoutFiltering.getDashboardPresets()!=null &&!cellToLayoutFiltering.getDashboardPresets().isEmpty()){
			Set<String> ids=cellToLayoutFiltering.getDashboardPresets().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<CellToLayout, DashboardPreset> join=r.join(CellToLayout_.dashboardPreset);
			preds.add(join.get(DashboardPreset_.id).in(ids));
		}

		if(cellToLayoutFiltering.getGridLayoutCells()!=null &&!cellToLayoutFiltering.getGridLayoutCells().isEmpty()){
			Set<String> ids=cellToLayoutFiltering.getGridLayoutCells().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<CellToLayout, GridLayoutCell> join=r.join(CellToLayout_.gridLayoutCell);
			preds.add(join.get(GridLayoutCell_.id).in(ids));
		}



	}

	public long countAllCellToLayout(CellToLayoutFiltering cellToLayoutFiltering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<CellToLayout> r = q.from(CellToLayout.class);
		List<Predicate> preds = new ArrayList<>();
		addCellToLayoutPredicates(preds, cb, r, cellToLayoutFiltering);
		QueryInformationHolder<CellToLayout> queryInformationHolder = new QueryInformationHolder<>(
				cellToLayoutFiltering, CellToLayout.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}
}
