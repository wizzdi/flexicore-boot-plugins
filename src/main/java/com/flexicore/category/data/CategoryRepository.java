/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.category.model.Category;
import com.flexicore.category.model.CategoryFilter;
import com.flexicore.category.model.Category_;
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
public class CategoryRepository extends AbstractRepositoryPlugin implements ServicePlugin {

	public CategoryRepository() {
		// TODO Auto-generated constructor stub
	}

	public long countAllCategories(CategoryFilter categoryFilter, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Category> r = q.from(Category.class);
		List<Predicate> preds = new ArrayList<>();
		addCategoriesPredicates(categoryFilter, r, q, cb, preds);
		QueryInformationHolder<Category> queryInformationHolder = new QueryInformationHolder<>(categoryFilter, Category.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public List<Category> listAllCategories(CategoryFilter categoryFilter, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Category> q = cb.createQuery(Category.class);
		Root<Category> r = q.from(Category.class);
		List<Predicate> preds = new ArrayList<>();
		addCategoriesPredicates(categoryFilter, r, q, cb, preds);
		QueryInformationHolder<Category> queryInformationHolder = new QueryInformationHolder<>(categoryFilter, Category.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addCategoriesPredicates(CategoryFilter categoryFilter, Root<Category> r, CriteriaQuery<?> q, CriteriaBuilder cb, List<Predicate> preds) {
		if(categoryFilter.getNames()!=null && !categoryFilter.getNames().isEmpty()){
			preds.add(r.get(Category_.name).in(categoryFilter.getNames()));
		}
	}

}
