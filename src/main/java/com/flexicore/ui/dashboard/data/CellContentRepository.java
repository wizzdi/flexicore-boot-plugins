package com.flexicore.ui.dashboard.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.CellContent;
import com.flexicore.ui.dashboard.request.CellContentFiltering;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@PluginInfo(version = 1)
@Extension
@Component
public class CellContentRepository extends AbstractRepositoryPlugin {

	public List<CellContent> listAllCellContent(CellContentFiltering cellContentFiltering,
														SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CellContent> q = cb.createQuery(CellContent.class);
		Root<CellContent> r = q.from(CellContent.class);
		List<Predicate> preds = new ArrayList<>();
		addCellContentPredicates(preds, cb, r, cellContentFiltering);
		QueryInformationHolder<CellContent> queryInformationHolder = new QueryInformationHolder<>(
				cellContentFiltering, CellContent.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addCellContentPredicates(List<Predicate> preds, CriteriaBuilder cb,
			Root<CellContent> r, CellContentFiltering cellContentFiltering) {




	}

	public long countAllCellContent(CellContentFiltering cellContentFiltering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<CellContent> r = q.from(CellContent.class);
		List<Predicate> preds = new ArrayList<>();
		addCellContentPredicates(preds, cb, r, cellContentFiltering);
		QueryInformationHolder<CellContent> queryInformationHolder = new QueryInformationHolder<>(
				cellContentFiltering, CellContent.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}
}
