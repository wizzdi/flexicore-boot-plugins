/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.service;


import com.flexicore.category.data.CategoryToBaseclassRepository;
import com.flexicore.category.model.CategoryToBaseClass;
import com.flexicore.category.request.CategoryToBaseclassCreate;
import com.flexicore.category.request.CategoryToBaseclassFilter;
import com.flexicore.category.request.CategoryToBaseclassUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Extension

@Component
public class CategoryToBaseclassService implements Plugin {

    @Autowired
    private BasicService basicService;

    @Autowired
    
    private CategoryToBaseclassRepository categoryToBaseclassRepository;

    public PaginationResponse<CategoryToBaseClass> getAllCategoryToBaseclass(CategoryToBaseclassFilter categoryToBaseclassFilter, SecurityContextBase securityContext) {
        List<CategoryToBaseClass> categoryToBaseClasses=listAllCategoryToBaseclass(categoryToBaseclassFilter,securityContext);
        long count= categoryToBaseclassRepository.countAllCategoryToBaseclass(categoryToBaseclassFilter,securityContext);
        return new PaginationResponse<>(categoryToBaseClasses,categoryToBaseclassFilter,count);
    }


    public List<CategoryToBaseClass> listAllCategoryToBaseclass(CategoryToBaseclassFilter categoryToBaseclassFilter, SecurityContextBase securityContext) {
        return categoryToBaseclassRepository.listAllCategoryToBaseclass(categoryToBaseclassFilter,securityContext);
    }


    public CategoryToBaseClass createCategoryToBaseclassNoMerge(CategoryToBaseclassCreate categoryToBaseclassCreate, SecurityContextBase securityContext) {
        CategoryToBaseClass categoryToBaseClass=new CategoryToBaseClass();
        categoryToBaseClass.setId(UUID.randomUUID().toString());
        updateCategoryToBaseclassNoMerge(categoryToBaseclassCreate,categoryToBaseClass);
        BaseclassService.createSecurityObjectNoMerge(categoryToBaseClass,securityContext);
        return categoryToBaseClass;
    }


    public CategoryToBaseClass createCategoryToBaseclass(CategoryToBaseclassCreate categoryToBaseclassCreate, SecurityContextBase securityContext) {
        CategoryToBaseClass categoryToBaseClass=createCategoryToBaseclassNoMerge(categoryToBaseclassCreate,securityContext);
        categoryToBaseclassRepository.merge(categoryToBaseClass);
        return categoryToBaseClass;
    }


    public CategoryToBaseClass updateCategoryToBaseclass(CategoryToBaseclassUpdate categoryToBaseclassUpdate, SecurityContextBase securityContext) {
        CategoryToBaseClass categoryToBaseClass=categoryToBaseclassUpdate.getCategoryToBaseClass();
        if(updateCategoryToBaseclassNoMerge(categoryToBaseclassUpdate,categoryToBaseClass)){
            categoryToBaseclassRepository.merge(categoryToBaseClass);
        }
        return categoryToBaseClass;
    }


    public boolean updateCategoryToBaseclassNoMerge(CategoryToBaseclassCreate categoryToBaseclassCreate, CategoryToBaseClass categoryToBaseClass) {
        boolean update= basicService.updateBasicNoMerge(categoryToBaseclassCreate,categoryToBaseClass);
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

    public void validate(CategoryToBaseclassCreate categoryToBaseclassCreate, SecurityContextBase securityContext) {
        basicService.validate(categoryToBaseclassCreate, securityContext);
    }

    public void validate(CategoryToBaseclassFilter filtering, SecurityContextBase securityContext) {

        basicService.validate(filtering, securityContext);
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return categoryToBaseclassRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return categoryToBaseclassRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return categoryToBaseclassRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return categoryToBaseclassRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return categoryToBaseclassRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return categoryToBaseclassRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return categoryToBaseclassRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        categoryToBaseclassRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        categoryToBaseclassRepository.massMerge(toMerge);
    }
}
