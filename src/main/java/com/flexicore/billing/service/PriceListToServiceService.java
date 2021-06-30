package com.flexicore.billing.service;


import com.flexicore.billing.data.PriceListToServiceRepository;
import com.flexicore.billing.model.*;
import com.flexicore.billing.model.Currency;
import com.flexicore.billing.request.PriceListToServiceCreate;
import com.flexicore.billing.request.PriceListToServiceFiltering;
import com.flexicore.billing.request.PriceListToServiceUpdate;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class PriceListToServiceService implements Plugin {

        @Autowired
    private PriceListToServiceRepository repository;

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

    public void validateFiltering(PriceListToServiceFiltering filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);
        Set<String> businessServiceIds = filtering.getBusinessServiceIds();
        Map<String, BusinessService> priceListToServiceMap = businessServiceIds.isEmpty() ? new HashMap<>() : listByIds(BusinessService.class, businessServiceIds,BusinessService_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        businessServiceIds.removeAll(priceListToServiceMap.keySet());
        if (!businessServiceIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No BusinessService with ids " + businessServiceIds);
        }
        filtering.setBusinessServices(new ArrayList<>(priceListToServiceMap.values()));

        Set<String> currencyIds = filtering.getCurrencyIds();
        Map<String, Currency> currencyMap = currencyIds.isEmpty() ? new HashMap<>() : listByIds(Currency.class, currencyIds, Currency_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        currencyIds.removeAll(currencyMap.keySet());
        if (!currencyIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Currency with ids " + currencyIds);
        }
        filtering.setCurrencies(new ArrayList<>(currencyMap.values()));

        Set<String> priceListIds = filtering.getPriceListIds();
        Map<String, PriceList> priceListMap = priceListIds.isEmpty() ? new HashMap<>() : listByIds(PriceList.class, priceListIds,PriceList_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        priceListIds.removeAll(priceListMap.keySet());
        if (!priceListIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No PriceList with ids " + priceListIds);
        }
        filtering.setPriceLists(new ArrayList<>(priceListMap.values()));
    }

    public PaginationResponse<PriceListToService> getAllPriceListToServices(
            SecurityContextBase securityContext, PriceListToServiceFiltering filtering) {
        List<PriceListToService> list = listAllPriceListToServices(securityContext, filtering);
        long count = repository.countAllPriceListToServices(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<PriceListToService> listAllPriceListToServices(SecurityContextBase securityContext, PriceListToServiceFiltering filtering) {
        return repository.getAllPriceListToServices(securityContext, filtering);
    }

    public PriceListToService createPriceListToService(PriceListToServiceCreate creationContainer,
                                                       SecurityContextBase securityContext) {
        PriceListToService priceListToService = createPriceListToServiceNoMerge(creationContainer, securityContext);
        repository.merge(priceListToService);
        return priceListToService;
    }

    private PriceListToService createPriceListToServiceNoMerge(PriceListToServiceCreate creationContainer,
                                                               SecurityContextBase securityContext) {
        PriceListToService priceListToService = new PriceListToService();
        priceListToService.setId(Baseclass.getBase64ID());

        updatePriceListToServiceNoMerge(priceListToService, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(priceListToService,securityContext);

        return priceListToService;
    }

    private boolean updatePriceListToServiceNoMerge(PriceListToService priceListToService,
                                                    PriceListToServiceCreate creationContainer) {
        boolean update = basicService.updateBasicNoMerge(creationContainer, priceListToService);
        if (creationContainer.getBusinessService() != null && (priceListToService.getBusinessService() == null || !creationContainer.getBusinessService().getId().equals(priceListToService.getBusinessService().getId()))) {
            priceListToService.setBusinessService(creationContainer.getBusinessService());
            update = true;
        }

        if (creationContainer.getCurrency() != null && (priceListToService.getCurrency() == null || !creationContainer.getCurrency().getId().equals(priceListToService.getCurrency().getId()))) {
            priceListToService.setCurrency(creationContainer.getCurrency());
            update = true;
        }

        if (creationContainer.getPriceList() != null && (priceListToService.getPriceList() == null || !creationContainer.getPriceList().getId().equals(priceListToService.getPriceList().getId()))) {
            priceListToService.setPriceList(creationContainer.getPriceList());
            update = true;
        }

        if (creationContainer.getPrice() != null && !creationContainer.getPrice().equals(priceListToService.getPrice())) {
            priceListToService.setPrice(creationContainer.getPrice());
            update = true;
        }
        if (creationContainer.getBillingCycleGranularity() != null && !creationContainer.getBillingCycleGranularity().equals(priceListToService.getBillingCycleGranularity())) {
            priceListToService.setBillingCycleGranularity(creationContainer.getBillingCycleGranularity());
            update = true;
        }
        if (creationContainer.getPaymentType() != null && !creationContainer.getPaymentType().equals(priceListToService.getPaymentType())) {
            priceListToService.setPaymentType(creationContainer.getPaymentType());
            update = true;
        }
        if (creationContainer.getTotalCycles() != null && !creationContainer.getTotalCycles().equals(priceListToService.getTotalCycles())) {
            priceListToService.setTotalCycles(creationContainer.getTotalCycles());
            update = true;
        }
        if (creationContainer.getBillingCycleInterval() != null && !creationContainer.getBillingCycleInterval().equals(priceListToService.getBillingCycleInterval())) {
            priceListToService.setBillingCycleInterval(creationContainer.getBillingCycleInterval());
            update = true;
        }
        return update;
    }

    public PriceListToService updatePriceListToService(PriceListToServiceUpdate updateContainer,
                                                       SecurityContextBase securityContext) {
        PriceListToService priceListToService = updateContainer.getPriceListToService();
        if (updatePriceListToServiceNoMerge(priceListToService, updateContainer)) {
            repository.merge(priceListToService);
        }
        return priceListToService;
    }

    public void validate(PriceListToServiceCreate creationContainer,
                         SecurityContextBase securityContext) {
        basicService.validate(creationContainer, securityContext);

        String currencyId = creationContainer.getCurrencyId();
        Currency currency = currencyId == null ? null : getByIdOrNull(currencyId, Currency.class, Currency_.security, securityContext);
        if (currency == null && currencyId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Currency with id " + currencyId);
        }
        creationContainer.setCurrency(currency);

        String businessServiceId = creationContainer.getBusinessServiceId();
        BusinessService businessService = businessServiceId == null ? null : getByIdOrNull(businessServiceId, BusinessService.class, BusinessService_.security, securityContext);
        if (businessService == null && businessServiceId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No BusinessService with id " + businessServiceId);
        }
        creationContainer.setBusinessService(businessService);

        String priceListId = creationContainer.getPriceListId();
        PriceList priceList = priceListId == null ? null : getByIdOrNull(priceListId, PriceList.class, PriceList_.security, securityContext);
        if (priceList == null && priceListId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No PriceList with id " + priceListId);
        }
        creationContainer.setPriceList(priceList);
    }
}