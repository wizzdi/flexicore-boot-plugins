/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.category.data.CategoryRepository;
import com.flexicore.category.model.Category;
import com.flexicore.category.model.CategoryFilter;
import com.flexicore.category.request.CategoryCreate;
import com.flexicore.category.request.CategoryUpdate;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Extension
@PluginInfo(version = 1)
@Component
public class CategoryService implements ServicePlugin {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    @PluginInfo(version = 1)
    private CategoryRepository categoryRepository;

    @Autowired
    private BaseclassNewService baseclassNewService;


    public CategoryService() {
        // TODO Auto-generated constructor stub
    }


    public PaginationResponse<Category> getAllCategories(CategoryFilter categoryFilter, SecurityContext securityContext) {
        QueryInformationHolder<Category> cats = new QueryInformationHolder<>(categoryFilter, Category.class, securityContext);

        List<Category> list = categoryRepository.getAllFiltered(cats);
        long count = categoryRepository.countAllFiltered(cats);
        return new PaginationResponse<>(list, categoryFilter, count);
    }


    public List<Category> listAllCategories(CategoryFilter categoryFilter, SecurityContext securityContext) {
        return categoryRepository.listAllCategories(categoryFilter, securityContext);
    }


    public Category createCategoryNoMerge(CategoryCreate categoryCreate, SecurityContext securityContext) {
        Category category = new Category(categoryCreate.getName(), securityContext);
        updateCategoryNoMerge(categoryCreate, category);
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
        return baseclassNewService.updateBaseclassNoMerge(categoryCreate, category);
    }


    public void validate(CategoryCreate categoryCreate, SecurityContext securityContext) {
        baseclassNewService.validate(categoryCreate, securityContext);
    }

    public void validate(CategoryFilter filtering, SecurityContext securityContext) {

        baseclassNewService.validateFilter(filtering, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return categoryRepository.getByIdOrNull(id, c, batchString, securityContext);
    }
}
