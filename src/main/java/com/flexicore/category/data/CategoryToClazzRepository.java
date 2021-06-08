/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.data;

import com.flexicore.category.model.CategoryToClazz;
import com.flexicore.category.request.CategoryToClazzFilter;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Component
@Extension

public class CategoryToClazzRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private SecuredBasicRepository securedBasicRepository;

	public CategoryToClazzRepository() {
		// TODO Auto-generated constructor stub
	}


	public List<CategoryToClazz> listAllCategoryToClazz(CategoryToClazzFilter categoryToClazzFilter, SecurityContextBase securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CategoryToClazz> q = cb.createQuery(CategoryToClazz.class);
		Root<CategoryToClazz> r = q.from(CategoryToClazz.class);
		List<Predicate> preds = new ArrayList<>();
		addCategoryToClazzPredicates(categoryToClazzFilter, r, q, cb, preds,securityContext);
		q.select(r).where(preds.toArray(new Predicate[0]));
		TypedQuery<CategoryToClazz> query=em.createQuery(q);
		BasicRepository.addPagination(categoryToClazzFilter,query);
		return query.getResultList();
	}

	public <T extends CategoryToClazz> void addCategoryToClazzPredicates(CategoryToClazzFilter categoryToClazzFilter, From<?,T> r,
																		 CommonAbstractCriteria q, CriteriaBuilder cb, List<Predicate> preds, SecurityContextBase securityContext) {
		securedBasicRepository.addSecuredBasicPredicates(null,cb,q,r,preds,securityContext);


	}

	public long countAllCategoryToClazz(CategoryToClazzFilter categoryToClazzFilter, SecurityContextBase securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<CategoryToClazz> r = q.from(CategoryToClazz.class);
		List<Predicate> preds = new ArrayList<>();
		addCategoryToClazzPredicates(categoryToClazzFilter, r, q, cb, preds, securityContext);
		q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
		TypedQuery<Long> query=em.createQuery(q);
		BasicRepository.addPagination(categoryToClazzFilter,query);
		return query.getSingleResult();
	}


	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return securedBasicRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return securedBasicRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
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
