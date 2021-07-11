package com.wizzdi.flexicore.pricing.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.data.FrequencyRepository;
import com.wizzdi.flexicore.pricing.model.price.Frequency;
import com.wizzdi.flexicore.pricing.request.FrequencyCreate;
import com.wizzdi.flexicore.pricing.request.FrequencyFiltering;
import com.wizzdi.flexicore.pricing.request.FrequencyUpdate;
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

public class FrequencyService implements Plugin {

    @Autowired
    private FrequencyRepository repository;

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

    public void validateFiltering(FrequencyFiltering filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);
    }

    public PaginationResponse<Frequency> getAllFrequencies(
            SecurityContextBase securityContext, FrequencyFiltering filtering) {
        List<Frequency> list = listAllFrequencies(securityContext, filtering);
        long count = repository.countAllFrequencies(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Frequency> listAllFrequencies(SecurityContextBase securityContext, FrequencyFiltering filtering) {
        return repository.getAllFrequencies(securityContext, filtering);
    }

    public Frequency createFrequency(FrequencyCreate creationContainer,
                                     SecurityContextBase securityContext) {
        Frequency frequency = createFrequencyNoMerge(creationContainer, securityContext);
        repository.merge(frequency);
        return frequency;
    }

    private Frequency createFrequencyNoMerge(FrequencyCreate creationContainer,
                                             SecurityContextBase securityContext) {
        Frequency frequency = new Frequency();
        frequency.setId(Baseclass.getBase64ID());

        updateFrequencyNoMerge(frequency, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(frequency, securityContext);
        return frequency;
    }

    private boolean updateFrequencyNoMerge(Frequency frequency,
                                           FrequencyCreate creationContainer) {
        boolean update = basicService.updateBasicNoMerge(creationContainer, frequency);
        if(creationContainer.getIntervalCount()!=null&&!creationContainer.getIntervalCount().equals(frequency.getIntervalCount())){
            frequency.setIntervalCount(creationContainer.getIntervalCount());
            update=true;
        }
        if(creationContainer.getIntervalUnit()!=null&&!creationContainer.getIntervalUnit().equals(frequency.getIntervalUnit())){
            frequency.setIntervalUnit(creationContainer.getIntervalUnit());
            update=true;
        }


        return update;
    }

    public Frequency updateFrequency(FrequencyUpdate updateContainer,
                                     SecurityContextBase securityContext) {
        Frequency frequency = updateContainer.getFrequency();
        if (updateFrequencyNoMerge(frequency, updateContainer)) {
            repository.merge(frequency);
        }
        return frequency;
    }

    public void validate(FrequencyCreate creationContainer,
                         SecurityContextBase securityContext) {
        basicService.validate(creationContainer, securityContext);
    }
}