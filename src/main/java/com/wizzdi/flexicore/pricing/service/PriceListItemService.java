package com.wizzdi.flexicore.pricing.service;


import com.wizzdi.flexicore.pricing.data.PriceListItemRepository;
import com.wizzdi.flexicore.pricing.model.price.*;
import com.wizzdi.flexicore.pricing.model.price.Currency;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct_;
import com.wizzdi.flexicore.pricing.request.PriceListItemCreate;
import com.wizzdi.flexicore.pricing.request.PriceListItemFiltering;
import com.wizzdi.flexicore.pricing.request.PriceListItemUpdate;
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

public class PriceListItemService implements Plugin {

    @Autowired
    private PriceListItemRepository repository;

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

    public void validateFiltering(PriceListItemFiltering filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);
        Set<String> pricedProductIds = filtering.getPricedProductIds();
        Map<String, PricedProduct> priceListItemMap = pricedProductIds.isEmpty() ? new HashMap<>() : listByIds(PricedProduct.class, pricedProductIds, PricedProduct_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        pricedProductIds.removeAll(priceListItemMap.keySet());
        if (!pricedProductIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PricedProduct with ids " + pricedProductIds);
        }
        filtering.setPricedProducts(new ArrayList<>(priceListItemMap.values()));

        Set<String> priceListIds = filtering.getPriceListIds();
        Map<String, PriceList> priceListMap = priceListIds.isEmpty() ? new HashMap<>() : listByIds(PriceList.class, priceListIds, PriceList_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        priceListIds.removeAll(priceListMap.keySet());
        if (!priceListIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PriceList with ids " + priceListIds);
        }
        filtering.setPriceLists(new ArrayList<>(priceListMap.values()));
    }

    public PaginationResponse<PriceListItem> getAllPriceListItems(
            SecurityContextBase securityContext, PriceListItemFiltering filtering) {
        List<PriceListItem> list = listAllPriceListItems(securityContext, filtering);
        long count = repository.countAllPriceListItems(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<PriceListItem> listAllPriceListItems(SecurityContextBase securityContext, PriceListItemFiltering filtering) {
        return repository.getAllPriceListItems(securityContext, filtering);
    }

    public PriceListItem createPriceListItem(PriceListItemCreate creationContainer,
                                             SecurityContextBase securityContext) {
        PriceListItem priceListItem = createPriceListItemNoMerge(creationContainer, securityContext);
        repository.merge(priceListItem);
        return priceListItem;
    }

    private PriceListItem createPriceListItemNoMerge(PriceListItemCreate creationContainer,
                                                     SecurityContextBase securityContext) {
        PriceListItem priceListItem = new PriceListItem();
        priceListItem.setId(Baseclass.getBase64ID());

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
                                             SecurityContextBase securityContext) {
        PriceListItem priceListItem = updateContainer.getPriceListItem();
        if (updatePriceListItemNoMerge(priceListItem, updateContainer)) {
            repository.merge(priceListItem);
        }
        return priceListItem;
    }

    public void validate(PriceListItemCreate creationContainer,
                         SecurityContextBase securityContext) {
        basicService.validate(creationContainer, securityContext);

        String priceId = creationContainer.getPriceId();
        Price price = priceId == null ? null : getByIdOrNull(priceId, Price.class, Currency_.security, securityContext);
        if (price == null && priceId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Price with id " + priceId);
        }
        creationContainer.setPrice(price);

        String pricedProductId = creationContainer.getPricedProductId();
        PricedProduct pricedProduct = pricedProductId == null ? null : getByIdOrNull(pricedProductId, PricedProduct.class, PricedProduct_.security, securityContext);
        if (pricedProduct == null && pricedProductId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PricedProduct with id " + pricedProductId);
        }
        creationContainer.setPricedProduct(pricedProduct);

        String priceListId = creationContainer.getPriceListId();
        PriceList priceList = priceListId == null ? null : getByIdOrNull(priceListId, PriceList.class, PriceList_.security, securityContext);
        if (priceList == null && priceListId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PriceList with id " + priceListId);
        }
        creationContainer.setPriceList(priceList);
    }
}