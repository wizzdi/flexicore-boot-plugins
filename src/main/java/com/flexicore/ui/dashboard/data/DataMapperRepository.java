package com.flexicore.ui.dashboard.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.model.dynamic.DynamicExecution_;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.*;
import com.flexicore.ui.dashboard.request.DataMapperFiltering;
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
public class DataMapperRepository extends AbstractRepositoryPlugin {

	public List<DataMapper> listAllDataMapper(DataMapperFiltering dataMapperFiltering,
														SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DataMapper> q = cb.createQuery(DataMapper.class);
		Root<DataMapper> r = q.from(DataMapper.class);
		List<Predicate> preds = new ArrayList<>();
		addDataMapperPredicates(preds, cb, r, dataMapperFiltering);
		QueryInformationHolder<DataMapper> queryInformationHolder = new QueryInformationHolder<>(
				dataMapperFiltering, DataMapper.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addDataMapperPredicates(List<Predicate> preds, CriteriaBuilder cb,
			Root<DataMapper> r, DataMapperFiltering dataMapperFiltering) {

		if(dataMapperFiltering.getCellContentElements()!=null &&!dataMapperFiltering.getCellContentElements().isEmpty()){
			Set<String> ids=dataMapperFiltering.getCellContentElements().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<DataMapper, CellContentElement> join=r.join(DataMapper_.cellContentElement);
			preds.add(join.get(CellContentElement_.id).in(ids));
		}

		if(dataMapperFiltering.getCellToLayouts()!=null &&!dataMapperFiltering.getCellToLayouts().isEmpty()){
			Set<String> ids=dataMapperFiltering.getCellToLayouts().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<DataMapper, CellToLayout> join=r.join(DataMapper_.cellToLayout);
			preds.add(join.get(CellToLayout_.id).in(ids));
		}

		if(dataMapperFiltering.getDynamicExecutions()!=null &&!dataMapperFiltering.getDynamicExecutions().isEmpty()){
			Set<String> ids=dataMapperFiltering.getDynamicExecutions().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<DataMapper, DynamicExecution> join=r.join(DataMapper_.dynamicExecution);
			preds.add(join.get(DynamicExecution_.id).in(ids));
		}



	}

	public long countAllDataMapper(DataMapperFiltering dataMapperFiltering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<DataMapper> r = q.from(DataMapper.class);
		List<Predicate> preds = new ArrayList<>();
		addDataMapperPredicates(preds, cb, r, dataMapperFiltering);
		QueryInformationHolder<DataMapper> queryInformationHolder = new QueryInformationHolder<>(
				dataMapperFiltering, DataMapper.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}
}
