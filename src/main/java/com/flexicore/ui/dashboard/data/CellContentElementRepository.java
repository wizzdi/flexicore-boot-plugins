package com.flexicore.ui.dashboard.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.*;
import com.flexicore.ui.dashboard.request.CellContentElementFiltering;
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
public class CellContentElementRepository extends AbstractRepositoryPlugin {

	public List<CellContentElement> listAllCellContentElement(CellContentElementFiltering cellContentElementFiltering,
														SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CellContentElement> q = cb.createQuery(CellContentElement.class);
		Root<CellContentElement> r = q.from(CellContentElement.class);
		List<Predicate> preds = new ArrayList<>();
		addCellContentElementPredicates(preds, cb, r, cellContentElementFiltering);
		QueryInformationHolder<CellContentElement> queryInformationHolder = new QueryInformationHolder<>(
				cellContentElementFiltering, CellContentElement.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addCellContentElementPredicates(List<Predicate> preds, CriteriaBuilder cb,
			Root<CellContentElement> r, CellContentElementFiltering cellContentElementFiltering) {

		if(cellContentElementFiltering.getCellContents()!=null &&!cellContentElementFiltering.getCellContents().isEmpty()){
			Set<String> ids=cellContentElementFiltering.getCellContents().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<CellContentElement, CellContent> join=r.join(CellContentElement_.cellContent);
			preds.add(join.get(CellContent_.id).in(ids));
		}


	}

	public long countAllCellContentElement(CellContentElementFiltering cellContentElementFiltering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<CellContentElement> r = q.from(CellContentElement.class);
		List<Predicate> preds = new ArrayList<>();
		addCellContentElementPredicates(preds, cb, r, cellContentElementFiltering);
		QueryInformationHolder<CellContentElement> queryInformationHolder = new QueryInformationHolder<>(
				cellContentElementFiltering, CellContentElement.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}
}
