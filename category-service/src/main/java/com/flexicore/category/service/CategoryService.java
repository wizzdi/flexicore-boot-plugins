/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.service;


import com.flexicore.category.data.CategoryRepository;
import com.flexicore.category.model.Category;
import com.flexicore.category.request.CategoryFilter;
import com.flexicore.category.request.CategoryCreate;
import com.flexicore.category.request.CategoryUpdate;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Extension

@Component
public class CategoryService implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired

    private CategoryRepository categoryRepository;

    @Autowired
    private BasicService basicService;


    public CategoryService() {
        // TODO Auto-generated constructor stub
    }


    public PaginationResponse<Category> getAllCategories(CategoryFilter categoryFilter, SecurityContext securityContext) {

        List<Category> list = categoryRepository.listAllCategories(categoryFilter, securityContext);
        long count = categoryRepository.countAllCategories(categoryFilter, securityContext);
        return new PaginationResponse<>(list, categoryFilter, count);
    }


    public List<Category> listAllCategories(CategoryFilter categoryFilter, SecurityContext securityContext) {
        return categoryRepository.listAllCategories(categoryFilter, securityContext);
    }


    public Category createCategoryNoMerge(CategoryCreate categoryCreate, SecurityContext securityContext) {
        Category category = new Category();
        category.setId(UUID.randomUUID().toString());
        updateCategoryNoMerge(categoryCreate, category);
        BaseclassService.createSecurityObjectNoMerge(category, securityContext);
        return category;
    }


    public Category createCategory(CategoryCreate categoryCreate, SecurityContext securityContext) {
        Category category = createCategoryNoMerge(categoryCreate, securityContext);
        categoryRepository.merge(category);
        return category;
    }


    public Category updateCategory(CategoryUpdate categoryUpdate, SecurityContext securityContext) {
        Category category = categoryUpdate.getCategory();
        if (updateCategoryNoMerge(categoryUpdate, category)) {
            categoryRepository.merge(category);
        }
        return category;
    }


    public boolean updateCategoryNoMerge(CategoryCreate categoryCreate, Category category) {
        return basicService.updateBasicNoMerge(categoryCreate, category);
    }


    public void validate(CategoryCreate categoryCreate, SecurityContext securityContext) {
        basicService.validate(categoryCreate, securityContext);
    }

    public void validate(CategoryFilter filtering, SecurityContext securityContext) {

        basicService.validate(filtering, securityContext);
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return categoryRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return categoryRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return categoryRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return categoryRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return categoryRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return categoryRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return categoryRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        categoryRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        categoryRepository.massMerge(toMerge);
    }
}
