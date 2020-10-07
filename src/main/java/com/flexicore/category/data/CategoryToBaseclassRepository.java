/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.category.model.*;
import com.flexicore.category.request.CategoryToBaseclassFilter;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.*;
import com.flexicore.security.SecurityContext;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Component
@Extension
@PluginInfo(version = 1)
public class CategoryToBaseclassRepository extends AbstractRepositoryPlugin implements ServicePlugin {

	public CategoryToBaseclassRepository() {
		// TODO Auto-generated constructor stub
	}


	public boolean deleteCategoryLinks(Set<String> toRemove) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<CategoryToBaseClass> q = cb.createCriteriaDelete(CategoryToBaseClass.class);
		Root<CategoryToBaseClass> r = q.from(CategoryToBaseClass.class);
		Predicate pred=r.get(Baselink_.id).in(toRemove);
		q.where(pred);
		Query query=em.createQuery(q);
		return query.executeUpdate()> 0;
	}

	public List<CategoryToBaseClass> listAllCategoryToBaseclass(CategoryToBaseclassFilter categoryToBaseclassFilter, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CategoryToBaseClass> q = cb.createQuery(CategoryToBaseClass.class);
		Root<CategoryToBaseClass> r = q.from(CategoryToBaseClass.class);
		List<Predicate> preds = new ArrayList<>();
		addCategoryToBaseclassPredicates(categoryToBaseclassFilter, r, q, cb, preds);
		QueryInformationHolder<CategoryToBaseClass> queryInformationHolder = new QueryInformationHolder<>(categoryToBaseclassFilter, CategoryToBaseClass.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	private void addCategoryToBaseclassPredicates(CategoryToBaseclassFilter categoryToBaseclassFilter, Root<CategoryToBaseClass> r, CriteriaQuery<?> q, CriteriaBuilder cb, List<Predicate> preds) {


	}

	public long countAllCategoryToBaseclass(CategoryToBaseclassFilter categoryToBaseclassFilter, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<CategoryToBaseClass> r = q.from(CategoryToBaseClass.class);
		List<Predicate> preds = new ArrayList<>();
		addCategoryToBaseclassPredicates(categoryToBaseclassFilter, r, q, cb, preds);
		QueryInformationHolder<CategoryToBaseClass> queryInformationHolder = new QueryInformationHolder<>(categoryToBaseclassFilter, CategoryToBaseClass.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}
}
