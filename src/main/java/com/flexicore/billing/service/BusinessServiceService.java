package com.flexicore.billing.service;


import com.flexicore.billing.data.BusinessServiceRepository;
import com.flexicore.billing.model.BusinessService;
import com.flexicore.billing.request.BusinessServiceCreate;
import com.flexicore.billing.request.BusinessServiceFiltering;
import com.flexicore.billing.request.BusinessServiceUpdate;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContextBase;

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

@Extension
@Component

public class BusinessServiceService implements Plugin {

    @Autowired
    private BusinessServiceRepository repository;

    @Autowired
    private BasicService basicService;

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return repository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return repository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return repository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        repository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        repository.massMerge(toMerge);
    }

    public void validateFiltering(BusinessServiceFiltering filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);
    }

    public PaginationResponse<BusinessService> getAllBusinessServices(
            SecurityContextBase securityContext, BusinessServiceFiltering filtering) {
        List<BusinessService> list = listAllBusinessServices(securityContext, filtering);
        long count = repository.countAllBusinessServices(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

	public List<BusinessService> listAllBusinessServices(SecurityContextBase securityContext, BusinessServiceFiltering filtering) {
		return repository.listAllBusinessServices(securityContext, filtering);
	}

	public BusinessService createBusinessService(BusinessServiceCreate creationContainer,
                                                 SecurityContextBase securityContext) {
        BusinessService businessService = createBusinessServiceNoMerge(creationContainer, securityContext);
        repository.merge(businessService);
        return businessService;
    }

    public BusinessService createBusinessServiceNoMerge(BusinessServiceCreate creationContainer,
                                                         SecurityContextBase securityContext) {
        BusinessService businessService = new BusinessService();
        businessService.setId(Baseclass.getBase64ID());
        updateBusinessServiceNoMerge(businessService, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(businessService, securityContext);
        return businessService;
    }

    public boolean updateBusinessServiceNoMerge(BusinessService businessService,
                                                 BusinessServiceCreate creationContainer) {
        boolean update = basicService.updateBasicNoMerge(creationContainer, businessService);

        return update;
    }

    public BusinessService updateBusinessService(BusinessServiceUpdate updateContainer,
                                                 SecurityContextBase securityContext) {
        BusinessService businessService = updateContainer.getBusinessService();
        if (updateBusinessServiceNoMerge(businessService, updateContainer)) {
            repository.merge(businessService);
        }
        return businessService;
    }

    public void validate(BusinessServiceCreate creationContainer,
                         SecurityContextBase securityContext) {
        basicService.validate(creationContainer, securityContext);
    }
}