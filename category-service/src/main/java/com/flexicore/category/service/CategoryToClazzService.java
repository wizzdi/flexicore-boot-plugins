/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.service;


import com.flexicore.category.data.CategoryToClazzRepository;
import com.flexicore.category.model.CategoryToClazz;
import com.flexicore.category.request.CategoryToClazzCreate;
import com.flexicore.category.request.CategoryToClazzFilter;
import com.flexicore.category.request.CategoryToClazzUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Extension

@Component
public class CategoryToClazzService implements Plugin {

    @Autowired
    private BasicService basicService;

    @Autowired
    
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
        CategoryToClazz categoryToClazz=new CategoryToClazz();
        categoryToClazz.setId(UUID.randomUUID().toString());
        updateCategoryToClazzNoMerge(categoryToClazzCreate,categoryToClazz);
        BaseclassService.createSecurityObjectNoMerge(categoryToClazz,securityContext);
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
        boolean update= basicService.updateBasicNoMerge(categoryToClazzCreate,categoryToClazz);
        if(categoryToClazzCreate.getCategory()!=null && (categoryToClazz.getCategory()==null||!categoryToClazzCreate.getCategory().getId().equals(categoryToClazz.getCategory().getId()))){
            categoryToClazz.setCategory(categoryToClazzCreate.getCategory());
            update=true;
        }
        if(categoryToClazzCreate.getClazz()!=null && !categoryToClazzCreate.getClazz().equals(categoryToClazz.getClazz())){
            categoryToClazz.setType(categoryToClazzCreate.getClazz().name());
            update=true;
        }
        return update;
    }


    public void validate(CategoryToClazzCreate categoryToBaseclassCreate, SecurityContext securityContext) {
        basicService.validate(categoryToBaseclassCreate, securityContext);
    }

    public void validate(CategoryToClazzFilter filtering, SecurityContext securityContext) {

        basicService.validate(filtering, securityContext);
    }


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return categoryToClazzRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return categoryToClazzRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return categoryToClazzRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return categoryToClazzRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return categoryToClazzRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return categoryToClazzRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return categoryToClazzRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        categoryToClazzRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        categoryToClazzRepository.massMerge(toMerge);
    }
}
