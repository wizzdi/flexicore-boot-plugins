/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.category.model.CategoryToClazz;
import com.flexicore.category.request.CategoryToClazzFilter;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;


@Component
@Extension
@PluginInfo(version = 1)
public class CategoryToClazzRepository extends AbstractRepositoryPlugin implements ServicePlugin {

	public CategoryToClazzRepository() {
		// TODO Auto-generated constructor stub
	}


	public List<CategoryToClazz> listAllCategoryToClazz(CategoryToClazzFilter categoryToClazzFilter, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CategoryToClazz> q = cb.createQuery(CategoryToClazz.class);
		Root<CategoryToClazz> r = q.from(CategoryToClazz.class);
		List<Predicate> preds = new ArrayList<>();
		addCategoryToClazzPredicates(categoryToClazzFilter, r, q, cb, preds);
		QueryInformationHolder<CategoryToClazz> queryInformationHolder = new QueryInformationHolder<>(categoryToClazzFilter, CategoryToClazz.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	private void addCategoryToClazzPredicates(CategoryToClazzFilter categoryToClazzFilter, Root<CategoryToClazz> r, CriteriaQuery<?> q, CriteriaBuilder cb, List<Predicate> preds) {


	}

	public long countAllCategoryToClazz(CategoryToClazzFilter categoryToClazzFilter, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<CategoryToClazz> r = q.from(CategoryToClazz.class);
		List<Predicate> preds = new ArrayList<>();
		addCategoryToClazzPredicates(categoryToClazzFilter, r, q, cb, preds);
		QueryInformationHolder<CategoryToClazz> queryInformationHolder = new QueryInformationHolder<>(categoryToClazzFilter, CategoryToClazz.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}
}
