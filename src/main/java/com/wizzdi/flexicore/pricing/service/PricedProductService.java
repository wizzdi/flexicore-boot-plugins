package com.wizzdi.flexicore.pricing.service;


import com.wizzdi.flexicore.pricing.data.PricedProductRepository;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.pricing.request.PricedProductCreate;
import com.wizzdi.flexicore.pricing.request.PricedProductFiltering;
import com.wizzdi.flexicore.pricing.request.PricedProductUpdate;
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

public class PricedProductService implements Plugin {

    @Autowired
    private PricedProductRepository repository;

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

    public void validateFiltering(PricedProductFiltering filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);
    }

    public PaginationResponse<PricedProduct> getAllPricedProducts(
            SecurityContextBase securityContext, PricedProductFiltering filtering) {
        List<PricedProduct> list = listAllPricedProducts(securityContext, filtering);
        long count = repository.countAllPricedProducts(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

	public List<PricedProduct> listAllPricedProducts(SecurityContextBase securityContext, PricedProductFiltering filtering) {
		return repository.listAllPricedProducts(securityContext, filtering);
	}

	public PricedProduct createPricedProduct(PricedProductCreate creationContainer,
                                                 SecurityContextBase securityContext) {
        PricedProduct pricedProduct = createPricedProductNoMerge(creationContainer, securityContext);
        repository.merge(pricedProduct);
        return pricedProduct;
    }

    public PricedProduct createPricedProductNoMerge(PricedProductCreate creationContainer,
                                                         SecurityContextBase securityContext) {
        PricedProduct pricedProduct = new PricedProduct();
        pricedProduct.setId(Baseclass.getBase64ID());
        updatePricedProductNoMerge(pricedProduct, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(pricedProduct, securityContext);
        return pricedProduct;
    }

    public boolean updatePricedProductNoMerge(PricedProduct pricedProduct,
                                                 PricedProductCreate creationContainer) {
        boolean update = basicService.updateBasicNoMerge(creationContainer, pricedProduct);

        return update;
    }

    public PricedProduct updatePricedProduct(PricedProductUpdate updateContainer,
                                                 SecurityContextBase securityContext) {
        PricedProduct pricedProduct = updateContainer.getPricedProduct();
        if (updatePricedProductNoMerge(pricedProduct, updateContainer)) {
            repository.merge(pricedProduct);
        }
        return pricedProduct;
    }

    public void validate(PricedProductCreate creationContainer,
                         SecurityContextBase securityContext) {
        basicService.validate(creationContainer, securityContext);
    }
}