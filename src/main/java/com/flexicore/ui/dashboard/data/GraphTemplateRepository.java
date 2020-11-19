package com.flexicore.ui.dashboard.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.CellContent;
import com.flexicore.ui.dashboard.model.GraphTemplate;
import com.flexicore.ui.dashboard.model.GraphTemplate_;
import com.flexicore.ui.dashboard.model.CellContent_;
import com.flexicore.ui.dashboard.request.GraphTemplateFiltering;
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
public class GraphTemplateRepository extends AbstractRepositoryPlugin {

	public List<GraphTemplate> listAllGraphTemplate(GraphTemplateFiltering graphTemplateFiltering,
														SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<GraphTemplate> q = cb.createQuery(GraphTemplate.class);
		Root<GraphTemplate> r = q.from(GraphTemplate.class);
		List<Predicate> preds = new ArrayList<>();
		addGraphTemplatePredicates(preds, cb, r, graphTemplateFiltering);
		QueryInformationHolder<GraphTemplate> queryInformationHolder = new QueryInformationHolder<>(
				graphTemplateFiltering, GraphTemplate.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addGraphTemplatePredicates(List<Predicate> preds, CriteriaBuilder cb,
			Root<GraphTemplate> r, GraphTemplateFiltering graphTemplateFiltering) {

	}

	public long countAllGraphTemplate(GraphTemplateFiltering graphTemplateFiltering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<GraphTemplate> r = q.from(GraphTemplate.class);
		List<Predicate> preds = new ArrayList<>();
		addGraphTemplatePredicates(preds, cb, r, graphTemplateFiltering);
		QueryInformationHolder<GraphTemplate> queryInformationHolder = new QueryInformationHolder<>(
				graphTemplateFiltering, GraphTemplate.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}
}
