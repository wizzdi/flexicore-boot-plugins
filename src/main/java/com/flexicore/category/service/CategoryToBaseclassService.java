/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.category.data.CategoryToBaseclassRepository;
import com.flexicore.category.model.CategoryFilter;
import com.flexicore.category.model.CategoryToBaseClass;
import com.flexicore.category.request.CategoryCreate;
import com.flexicore.category.request.CategoryToBaseclassCreate;
import com.flexicore.category.request.CategoryToBaseclassFilter;
import com.flexicore.category.request.CategoryToBaseclassUpdate;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Extension
@PluginInfo(version = 1)
@Component
public class CategoryToBaseclassService implements ServicePlugin {

    @Autowired
    private BaseclassNewService baseclassNewService;

    @Autowired
    @PluginInfo(version = 1)
    private CategoryToBaseclassRepository categoryToBaseclassRepository;

    public PaginationResponse<CategoryToBaseClass> getAllCategoryToBaseclass(CategoryToBaseclassFilter categoryToBaseclassFilter, SecurityContext securityContext) {
        List<CategoryToBaseClass> categoryToBaseClasses=listAllCategoryToBaseclass(categoryToBaseclassFilter,securityContext);
        long count= categoryToBaseclassRepository.countAllCategoryToBaseclass(categoryToBaseclassFilter,securityContext);
        return new PaginationResponse<>(categoryToBaseClasses,categoryToBaseclassFilter,count);
    }


    public List<CategoryToBaseClass> listAllCategoryToBaseclass(CategoryToBaseclassFilter categoryToBaseclassFilter, SecurityContext securityContext) {
        return categoryToBaseclassRepository.listAllCategoryToBaseclass(categoryToBaseclassFilter,securityContext);
    }


    public CategoryToBaseClass createCategoryToBaseclassNoMerge(CategoryToBaseclassCreate categoryToBaseclassCreate, SecurityContext securityContext) {
        CategoryToBaseClass categoryToBaseClass=new CategoryToBaseClass(categoryToBaseclassCreate.getName(),securityContext);
        updateCategoryToBaseclassNoMerge(categoryToBaseclassCreate,categoryToBaseClass);
        return categoryToBaseClass;
    }


    public CategoryToBaseClass createCategoryToBaseclass(CategoryToBaseclassCreate categoryToBaseclassCreate, SecurityContext securityContext) {
        CategoryToBaseClass categoryToBaseClass=createCategoryToBaseclassNoMerge(categoryToBaseclassCreate,securityContext);
        categoryToBaseclassRepository.merge(categoryToBaseClass);
        return categoryToBaseClass;
    }


    public CategoryToBaseClass updateCategoryToBaseclass(CategoryToBaseclassUpdate categoryToBaseclassUpdate, SecurityContext securityContext) {
        CategoryToBaseClass categoryToBaseClass=categoryToBaseclassUpdate.getCategoryToBaseClass();
        if(updateCategoryToBaseclassNoMerge(categoryToBaseclassUpdate,categoryToBaseClass)){
            categoryToBaseclassRepository.merge(categoryToBaseClass);
        }
        return categoryToBaseClass;
    }


    public boolean updateCategoryToBaseclassNoMerge(CategoryToBaseclassCreate categoryToBaseclassCreate, CategoryToBaseClass categoryToBaseClass) {
        boolean update=baseclassNewService.updateBaseclassNoMerge(categoryToBaseclassCreate,categoryToBaseClass);
        if(categoryToBaseclassCreate.getCategory()!=null && (categoryToBaseClass.getCategory()==null||!categoryToBaseclassCreate.getCategory().getId().equals(categoryToBaseClass.getCategory().getId()))){
            categoryToBaseClass.setCategory(categoryToBaseclassCreate.getCategory());
            update=true;
        }
        if(categoryToBaseclassCreate.getBaseclass()!=null && (categoryToBaseClass.getBaseclass()==null||!categoryToBaseclassCreate.getBaseclass().getId().equals(categoryToBaseClass.getBaseclass().getId()))){
            categoryToBaseClass.setBaseclass(categoryToBaseclassCreate.getBaseclass());
            update=true;
        }
        return update;
    }

    public void validate(CategoryToBaseclassCreate categoryToBaseclassCreate, SecurityContext securityContext) {
        baseclassNewService.validate(categoryToBaseclassCreate, securityContext);
    }

    public void validate(CategoryToBaseclassFilter filtering, SecurityContext securityContext) {

        baseclassNewService.validateFilter(filtering, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return categoryToBaseclassRepository.getByIdOrNull(id, c, batchString, securityContext);
    }
}
