package com.wizzdi.flexicore.pricing.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.data.RecurringPriceRepository;
import com.wizzdi.flexicore.pricing.model.price.RecurringPrice;
import com.wizzdi.flexicore.pricing.request.RecurringPriceCreate;
import com.wizzdi.flexicore.pricing.request.RecurringPriceFiltering;
import com.wizzdi.flexicore.pricing.request.RecurringPriceUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
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

public class RecurringPriceService implements Plugin {

    @Autowired
    private RecurringPriceRepository repository;

    @Autowired
    private PriceService priceService;

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
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

    public void validateFiltering(RecurringPriceFiltering filtering,
                                  SecurityContext securityContext) {
        priceService.validateFiltering(filtering, securityContext);
    }

    public PaginationResponse<RecurringPrice> getAllRecurringPrice(
            SecurityContext securityContext, RecurringPriceFiltering filtering) {
        List<RecurringPrice> list = listAllRecurringPrice(securityContext, filtering);
        long count = repository.countAllRecurringPrice(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<RecurringPrice> listAllRecurringPrice(SecurityContext securityContext, RecurringPriceFiltering filtering) {
        return repository.getAllRecurringPrice(securityContext, filtering);
    }

    public RecurringPrice createRecurringPrice(RecurringPriceCreate creationContainer,
                                               SecurityContext securityContext) {
        RecurringPrice recurringPrice = createRecurringPriceNoMerge(creationContainer, securityContext);
        repository.merge(recurringPrice);
        return recurringPrice;
    }

    private RecurringPrice createRecurringPriceNoMerge(RecurringPriceCreate creationContainer,
                                                       SecurityContext securityContext) {
        RecurringPrice recurringPrice = new RecurringPrice();
        recurringPrice.setId(UUID.randomUUID().toString());

        updateRecurringPriceNoMerge(recurringPrice, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(recurringPrice, securityContext);
        return recurringPrice;
    }

    private boolean updateRecurringPriceNoMerge(RecurringPrice recurringPrice,
                                                RecurringPriceCreate creationContainer) {
        boolean update = priceService.updatePriceNoMerge(recurringPrice, creationContainer);

        return update;
    }

    public RecurringPrice updateRecurringPrice(RecurringPriceUpdate updateContainer,
                                               SecurityContext securityContext) {
        RecurringPrice recurringPrice = updateContainer.getRecurringPrice();
        if (updateRecurringPriceNoMerge(recurringPrice, updateContainer)) {
            repository.merge(recurringPrice);
        }
        return recurringPrice;
    }

    public void validate(RecurringPriceCreate creationContainer,
                         SecurityContext securityContext) {
        priceService.validate(creationContainer, securityContext);
    }
}
