package com.wizzdi.flexicore.pricing.service;


import com.wizzdi.flexicore.pricing.data.PriceListItemRepository;
import com.wizzdi.flexicore.pricing.model.price.*;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct_;
import com.wizzdi.flexicore.pricing.request.PriceListItemCreate;
import com.wizzdi.flexicore.pricing.request.PriceListItemFiltering;
import com.wizzdi.flexicore.pricing.request.PriceListItemUpdate;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class PriceListItemService implements Plugin {

    @Autowired
    private PriceListItemRepository repository;

    @Autowired
    private BasicService basicService;

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

    public void validateFiltering(PriceListItemFiltering filtering,
                                  SecurityContext securityContext) {
        basicService.validate(filtering, securityContext);
        Set<String> pricedProductIds = filtering.getPricedProductIds();
        Map<String, PricedProduct> priceListItemMap = pricedProductIds.isEmpty() ? new HashMap<>() : listByIds(PricedProduct.class, pricedProductIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        pricedProductIds.removeAll(priceListItemMap.keySet());
        if (!pricedProductIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PricedProduct with ids " + pricedProductIds);
        }
        filtering.setPricedProducts(new ArrayList<>(priceListItemMap.values()));

        Set<String> priceListIds = filtering.getPriceListIds();
        Map<String, PriceList> priceListMap = priceListIds.isEmpty() ? new HashMap<>() : listByIds(PriceList.class, priceListIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        priceListIds.removeAll(priceListMap.keySet());
        if (!priceListIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PriceList with ids " + priceListIds);
        }
        filtering.setPriceLists(new ArrayList<>(priceListMap.values()));
    }

    public PaginationResponse<PriceListItem> getAllPriceListItems(
            SecurityContext securityContext, PriceListItemFiltering filtering) {
        List<PriceListItem> list = listAllPriceListItems(securityContext, filtering);
        long count = repository.countAllPriceListItems(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<PriceListItem> listAllPriceListItems(SecurityContext securityContext, PriceListItemFiltering filtering) {
        return repository.getAllPriceListItems(securityContext, filtering);
    }

    public PriceListItem createPriceListItem(PriceListItemCreate creationContainer,
                                             SecurityContext securityContext) {
        PriceListItem priceListItem = createPriceListItemNoMerge(creationContainer, securityContext);
        repository.merge(priceListItem);
        return priceListItem;
    }

    private PriceListItem createPriceListItemNoMerge(PriceListItemCreate creationContainer,
                                                     SecurityContext securityContext) {
        PriceListItem priceListItem = new PriceListItem();
        priceListItem.setId(UUID.randomUUID().toString());

        updatePriceListItemNoMerge(priceListItem, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(priceListItem, securityContext);

        return priceListItem;
    }

    private boolean updatePriceListItemNoMerge(PriceListItem priceListItem,
                                               PriceListItemCreate creationContainer) {
        boolean update = basicService.updateBasicNoMerge(creationContainer, priceListItem);

        if (creationContainer.getPriceList() != null && (priceListItem.getPriceList() == null || !creationContainer.getPriceList().getId().equals(priceListItem.getPriceList().getId()))) {
            priceListItem.setPriceList(creationContainer.getPriceList());
            update = true;
        }
        if (creationContainer.getPrice() != null && (priceListItem.getPrice() == null || !creationContainer.getPrice().getId().equals(priceListItem.getPrice().getId()))) {
            priceListItem.setPrice(creationContainer.getPrice());
            update = true;
        }

        if (creationContainer.getPricedProduct() != null && (priceListItem.getPricedProduct() == null || !creationContainer.getPricedProduct().getId().equals(priceListItem.getPricedProduct().getId()))) {
            priceListItem.setPricedProduct(creationContainer.getPricedProduct());
            update = true;
        }


        return update;
    }

    public PriceListItem updatePriceListItem(PriceListItemUpdate updateContainer,
                                             SecurityContext securityContext) {
        PriceListItem priceListItem = updateContainer.getPriceListItem();
        if (updatePriceListItemNoMerge(priceListItem, updateContainer)) {
            repository.merge(priceListItem);
        }
        return priceListItem;
    }

    public void validate(PriceListItemCreate creationContainer,
                         SecurityContext securityContext) {
        basicService.validate(creationContainer, securityContext);

        String priceId = creationContainer.getPriceId();
        Price price = priceId == null ? null : getByIdOrNull(priceId, Price.class,securityContext);
        if (price == null && priceId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Price with id " + priceId);
        }
        creationContainer.setPrice(price);

        String pricedProductId = creationContainer.getPricedProductId();
        PricedProduct pricedProduct = pricedProductId == null ? null : getByIdOrNull(pricedProductId, PricedProduct.class,securityContext);
        if (pricedProduct == null && pricedProductId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PricedProduct with id " + pricedProductId);
        }
        creationContainer.setPricedProduct(pricedProduct);

        String priceListId = creationContainer.getPriceListId();
        PriceList priceList = priceListId == null ? null : getByIdOrNull(priceListId, PriceList.class,securityContext);
        if (priceList == null && priceListId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PriceList with id " + priceListId);
        }
        creationContainer.setPriceList(priceList);
    }
}
