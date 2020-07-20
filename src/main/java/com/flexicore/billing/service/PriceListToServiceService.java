package com.flexicore.billing.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.data.PriceListToServiceRepository;
import com.flexicore.billing.model.BusinessService;
import com.flexicore.billing.model.Currency;
import com.flexicore.billing.model.PriceList;
import com.flexicore.billing.model.PriceListToService;
import com.flexicore.billing.request.PriceListToServiceCreate;
import com.flexicore.billing.request.PriceListToServiceFiltering;
import com.flexicore.billing.request.PriceListToServiceUpdate;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
@Primary
public class PriceListToServiceService implements ServicePlugin {

    @PluginInfo(version = 1)
    @Autowired
    private PriceListToServiceRepository repository;

    @Autowired
    private BaseclassNewService baseclassNewService;

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
                                                 List<String> batch, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batch, securityContext);
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids,
                                                   SecurityContext securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public void validateFiltering(PriceListToServiceFiltering filtering,
                                  SecurityContext securityContext) {
        baseclassNewService.validateFilter(filtering, securityContext);
        Set<String> businessServiceIds = filtering.getBusinessServiceIds();
        Map<String, BusinessService> priceListToServiceMap = businessServiceIds.isEmpty() ? new HashMap<>() : listByIds(BusinessService.class, businessServiceIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        businessServiceIds.removeAll(priceListToServiceMap.keySet());
        if (!businessServiceIds.isEmpty()) {
            throw new BadRequestException("No BusinessService with ids " + businessServiceIds);
        }
        filtering.setBusinessServices(new ArrayList<>(priceListToServiceMap.values()));

        Set<String> currencyIds = filtering.getCurrencyIds();
        Map<String, Currency> currencyMap = currencyIds.isEmpty() ? new HashMap<>() : listByIds(Currency.class, currencyIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        currencyIds.removeAll(currencyMap.keySet());
        if (!currencyIds.isEmpty()) {
            throw new BadRequestException("No Currency with ids " + currencyIds);
        }
        filtering.setCurrencies(new ArrayList<>(currencyMap.values()));

        Set<String> priceListIds = filtering.getPriceListIds();
        Map<String, PriceList> priceListMap = priceListIds.isEmpty() ? new HashMap<>() : listByIds(PriceList.class, priceListIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        priceListIds.removeAll(priceListMap.keySet());
        if (!priceListIds.isEmpty()) {
            throw new BadRequestException("No PriceList with ids " + priceListIds);
        }
        filtering.setPriceLists(new ArrayList<>(priceListMap.values()));
    }

    public PaginationResponse<PriceListToService> getAllPriceListToServices(
            SecurityContext securityContext, PriceListToServiceFiltering filtering) {
        List<PriceListToService> list = repository.getAllPriceListToServices(securityContext, filtering);
        long count = repository.countAllPriceListToServices(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public PriceListToService createPriceListToService(PriceListToServiceCreate creationContainer,
                                                       SecurityContext securityContext) {
        PriceListToService priceListToService = createPriceListToServiceNoMerge(creationContainer, securityContext);
        repository.merge(priceListToService);
        return priceListToService;
    }

    private PriceListToService createPriceListToServiceNoMerge(PriceListToServiceCreate creationContainer,
                                                               SecurityContext securityContext) {
        PriceListToService priceListToService = new PriceListToService(creationContainer.getName(), securityContext);
        updatePriceListToServiceNoMerge(priceListToService, creationContainer);
        return priceListToService;
    }

    private boolean updatePriceListToServiceNoMerge(PriceListToService priceListToService,
                                                    PriceListToServiceCreate creationContainer) {
        boolean update = baseclassNewService.updateBaseclassNoMerge(creationContainer, priceListToService);
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

        return update;
    }

    public PriceListToService updatePriceListToService(PriceListToServiceUpdate updateContainer,
                                                       SecurityContext securityContext) {
        PriceListToService priceListToService = updateContainer.getPriceListToService();
        if (updatePriceListToServiceNoMerge(priceListToService, updateContainer)) {
            repository.merge(priceListToService);
        }
        return priceListToService;
    }

    public void validate(PriceListToServiceCreate creationContainer,
                         SecurityContext securityContext) {
        baseclassNewService.validate(creationContainer, securityContext);

        String currencyId = creationContainer.getCurrencyId();
        Currency currency = currencyId == null ? null : getByIdOrNull(currencyId, Currency.class, null, securityContext);
        if (currency == null && currencyId != null) {
            throw new BadRequestException("No Currency with id " + currencyId);
        }
        creationContainer.setCurrency(currency);

        String businessServiceId = creationContainer.getBusinessServiceId();
        BusinessService businessService = businessServiceId == null ? null : getByIdOrNull(businessServiceId, BusinessService.class, null, securityContext);
        if (businessService == null && businessServiceId != null) {
            throw new BadRequestException("No BusinessService with id " + businessServiceId);
        }
        creationContainer.setBusinessService(businessService);

        String priceListId = creationContainer.getPriceListId();
        PriceList priceList = priceListId == null ? null : getByIdOrNull(priceListId, PriceList.class, null, securityContext);
        if (priceList == null && priceListId != null) {
            throw new BadRequestException("No PriceList with id " + priceListId);
        }
        creationContainer.setPriceList(priceList);
    }
}