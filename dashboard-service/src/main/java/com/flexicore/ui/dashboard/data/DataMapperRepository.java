package com.flexicore.ui.dashboard.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;

import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution_;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.ui.dashboard.model.*;
import com.flexicore.ui.dashboard.request.DataMapperFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Extension
@Component
public class DataMapperRepository implements Plugin {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private SecuredBasicRepository securedBasicRepository;

	public List<DataMapper> listAllDataMapper(DataMapperFilter dataMapperFilter, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DataMapper> q = cb.createQuery(DataMapper.class);
		Root<DataMapper> r = q.from(DataMapper.class);
		List<Predicate> preds = new ArrayList<>();
		addDataMapperPredicates(preds, cb,q, r, dataMapperFilter,securityContext);
  q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<DataMapper> query = em.createQuery(q);
        BasicRepository.addPagination(dataMapperFilter, query);
        return query.getResultList();
	}

	public <T extends DataMapper> void addDataMapperPredicates(List<Predicate> preds, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, DataMapperFilter dataMapperFilter, SecurityContext securityContext) {
		securedBasicRepository.addSecuredBasicPredicates(null,cb,q,r,preds,securityContext);

		if(dataMapperFilter.getCellContentElements()!=null &&!dataMapperFilter.getCellContentElements().isEmpty()){
			Set<String> ids= dataMapperFilter.getCellContentElements().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<T, CellContentElement> join=r.join(DataMapper_.cellContentElement);
			preds.add(join.get(CellContentElement_.id).in(ids));
		}

		if(dataMapperFilter.getCellToLayouts()!=null &&!dataMapperFilter.getCellToLayouts().isEmpty()){
			Set<String> ids= dataMapperFilter.getCellToLayouts().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<T, CellToLayout> join=r.join(DataMapper_.cellToLayout);
			preds.add(join.get(CellToLayout_.id).in(ids));
		}

		if(dataMapperFilter.getDynamicExecutions()!=null &&!dataMapperFilter.getDynamicExecutions().isEmpty()){
			Set<String> ids= dataMapperFilter.getDynamicExecutions().stream().map(f->f.getId()).collect(Collectors.toSet());

			preds.add(r.get(DataMapper_.dynamicExecution).get(DynamicExecution_.id).in(ids));
		}



	}

	public long countAllDataMapper(DataMapperFilter dataMapperFilter, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<DataMapper> r = q.from(DataMapper.class);
		List<Predicate> preds = new ArrayList<>();
		addDataMapperPredicates(preds, cb,q, r, dataMapperFilter,securityContext);
       q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
	}


	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return securedBasicRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return securedBasicRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return securedBasicRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return securedBasicRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return securedBasicRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return securedBasicRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		securedBasicRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		securedBasicRepository.massMerge(toMerge);
	}
}
