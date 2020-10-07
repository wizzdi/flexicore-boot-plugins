/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.category.data.CategoryToClazzRepository;
import com.flexicore.category.model.CategoryToClazz;
import com.flexicore.category.request.*;
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
public class CategoryToClazzService implements ServicePlugin {

    @Autowired
    private BaseclassNewService baseclassNewService;

    @Autowired
    @PluginInfo(version = 1)
    private CategoryToClazzRepository categoryToClazzRepository;

    public PaginationResponse<CategoryToClazz> getAllCategoryToClazz(CategoryToClazzFilter categoryToClazzFilter, SecurityContext securityContext) {
        List<CategoryToClazz> categoryToClazzes=listAllCategoryToClazz(categoryToClazzFilter,securityContext);
        long count= categoryToClazzRepository.countAllCategoryToClazz(categoryToClazzFilter,securityContext);
        return new PaginationResponse<>(categoryToClazzes,categoryToClazzFilter,count);
    }


    public List<CategoryToClazz> listAllCategoryToClazz(CategoryToClazzFilter categoryToClazzFilter, SecurityContext securityContext) {
        return categoryToClazzRepository.listAllCategoryToClazz(categoryToClazzFilter,securityContext);
    }


    public CategoryToClazz createCategoryToClazzNoMerge(CategoryToClazzCreate categoryToClazzCreate, SecurityContext securityContext) {
        CategoryToClazz categoryToClazz=new CategoryToClazz(categoryToClazzCreate.getName(),securityContext);
        updateCategoryToClazzNoMerge(categoryToClazzCreate,categoryToClazz);
        return categoryToClazz;
    }


    public CategoryToClazz createCategoryToClazz(CategoryToClazzCreate categoryToClazzCreate, SecurityContext securityContext) {
        CategoryToClazz categoryToClazz=createCategoryToClazzNoMerge(categoryToClazzCreate,securityContext);
        categoryToClazzRepository.merge(categoryToClazz);
        return categoryToClazz;
    }


    public CategoryToClazz updateCategoryToClazz(CategoryToClazzUpdate categoryToClazzUpdate, SecurityContext securityContext) {
        CategoryToClazz categoryToClazz=categoryToClazzUpdate.getCategoryToClazz();
        if(updateCategoryToClazzNoMerge(categoryToClazzUpdate,categoryToClazz)){
            categoryToClazzRepository.merge(categoryToClazz);
        }
        return categoryToClazz;
    }


    public boolean updateCategoryToClazzNoMerge(CategoryToClazzCreate categoryToClazzCreate, CategoryToClazz categoryToClazz) {
        boolean update=baseclassNewService.updateBaseclassNoMerge(categoryToClazzCreate,categoryToClazz);
        if(categoryToClazzCreate.getCategory()!=null && (categoryToClazz.getCategory()==null||!categoryToClazzCreate.getCategory().getId().equals(categoryToClazz.getCategory().getId()))){
            categoryToClazz.setCategory(categoryToClazzCreate.getCategory());
            update=true;
        }
        if(categoryToClazzCreate.getClazz()!=null && (categoryToClazz.getBaseclass()==null||!categoryToClazzCreate.getClazz().getId().equals(categoryToClazz.getBaseclass().getId()))){
            categoryToClazz.setBaseclass(categoryToClazzCreate.getClazz());
            update=true;
        }
        return update;
    }


    public void validate(CategoryToClazzCreate categoryToBaseclassCreate, SecurityContext securityContext) {
        baseclassNewService.validate(categoryToBaseclassCreate, securityContext);
    }

    public void validate(CategoryToClazzFilter filtering, SecurityContext securityContext) {

        baseclassNewService.validateFilter(filtering, securityContext);
    }


    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return categoryToClazzRepository.getByIdOrNull(id, c, batchString, securityContext);
    }
}
