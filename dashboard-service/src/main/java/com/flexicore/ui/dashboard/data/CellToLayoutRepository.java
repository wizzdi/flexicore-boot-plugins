package com.flexicore.ui.dashboard.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;

import com.flexicore.ui.dashboard.model.*;
import com.flexicore.ui.dashboard.request.CellToLayoutFilter;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Extension
@Component
public class CellToLayoutRepository implements Plugin{
@PersistenceContext
private EntityManager em;
@Autowired
private SecuredBasicRepository securedBasicRepository;

	public List<CellToLayout> listAllCellToLayout(CellToLayoutFilter cellToLayoutFilter,
                                                  SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CellToLayout> q = cb.createQuery(CellToLayout.class);
		Root<CellToLayout> r = q.from(CellToLayout.class);
		List<Predicate> preds = new ArrayList<>();
		addCellToLayoutPredicates(preds, cb,q, r, cellToLayoutFilter,securityContext);
  q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<CellToLayout> query = em.createQuery(q);
        BasicRepository.addPagination(cellToLayoutFilter, query);
        return query.getResultList();
	}

	public <T extends CellToLayout> void addCellToLayoutPredicates(List<Predicate> preds, CriteriaBuilder cb,
                                                                   CommonAbstractCriteria q, From<?,T> r, CellToLayoutFilter cellToLayoutFilter, SecurityContext securityContext) {

		if(cellToLayoutFilter.getCellContents()!=null &&!cellToLayoutFilter.getCellContents().isEmpty()){
			Set<String> ids= cellToLayoutFilter.getCellContents().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<T, CellContent> join=r.join(CellToLayout_.cellContent);
			preds.add(join.get(CellContent_.id).in(ids));
		}

		if(cellToLayoutFilter.getDashboardPresets()!=null &&!cellToLayoutFilter.getDashboardPresets().isEmpty()){
			Set<String> ids= cellToLayoutFilter.getDashboardPresets().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<T, DashboardPreset> join=r.join(CellToLayout_.dashboardPreset);
			preds.add(join.get(DashboardPreset_.id).in(ids));
		}

		if(cellToLayoutFilter.getGridLayoutCells()!=null &&!cellToLayoutFilter.getGridLayoutCells().isEmpty()){
			Set<String> ids= cellToLayoutFilter.getGridLayoutCells().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<T, GridLayoutCell> join=r.join(CellToLayout_.gridLayoutCell);
			preds.add(join.get(GridLayoutCell_.id).in(ids));
		}



	}

	public long countAllCellToLayout(CellToLayoutFilter cellToLayoutFilter,
                                     SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<CellToLayout> r = q.from(CellToLayout.class);
		List<Predicate> preds = new ArrayList<>();
		addCellToLayoutPredicates(preds, cb,q, r, cellToLayoutFilter,securityContext);
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
