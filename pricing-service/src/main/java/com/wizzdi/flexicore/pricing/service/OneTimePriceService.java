package com.wizzdi.flexicore.pricing.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.data.OneTimePriceRepository;
import com.wizzdi.flexicore.pricing.model.price.Frequency;
import com.wizzdi.flexicore.pricing.model.price.OneTimePrice;
import com.wizzdi.flexicore.pricing.model.price.PricingScheme;
import com.wizzdi.flexicore.pricing.request.OneTimePriceCreate;
import com.wizzdi.flexicore.pricing.request.OneTimePriceFiltering;
import com.wizzdi.flexicore.pricing.request.OneTimePriceUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;

@Extension
@Component

public class OneTimePriceService implements Plugin {

    @Autowired
    private OneTimePriceRepository repository;

    @Autowired
    private PriceService priceService;

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

    public void validateFiltering(OneTimePriceFiltering filtering,
                                  SecurityContextBase securityContext) {
        priceService.validateFiltering(filtering, securityContext);
    }

    public PaginationResponse<OneTimePrice> getAllOneTimePrice(
            SecurityContextBase securityContext, OneTimePriceFiltering filtering) {
        List<OneTimePrice> list = listAllOneTimePrice(securityContext, filtering);
        long count = repository.countAllOneTimePrice(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<OneTimePrice> listAllOneTimePrice(SecurityContextBase securityContext, OneTimePriceFiltering filtering) {
        return repository.getAllOneTimePrice(securityContext, filtering);
    }

    public OneTimePrice createOneTimePrice(OneTimePriceCreate creationContainer,
                                           SecurityContextBase securityContext) {
        OneTimePrice oneTimePrice = createOneTimePriceNoMerge(creationContainer, securityContext);
        repository.merge(oneTimePrice);
        return oneTimePrice;
    }

    private OneTimePrice createOneTimePriceNoMerge(OneTimePriceCreate creationContainer,
                                                   SecurityContextBase securityContext) {
        OneTimePrice oneTimePrice = new OneTimePrice();
        oneTimePrice.setId(Baseclass.getBase64ID());

        updateOneTimePriceNoMerge(oneTimePrice, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(oneTimePrice, securityContext);
        return oneTimePrice;
    }

    private boolean updateOneTimePriceNoMerge(OneTimePrice oneTimePrice,
                                              OneTimePriceCreate creationContainer) {
        boolean update = priceService.updatePriceNoMerge(oneTimePrice, creationContainer);
        if(creationContainer.getFrequency()!=null&&(oneTimePrice.getFrequency()==null||!creationContainer.getFrequency().getId().equals(oneTimePrice.getFrequency().getId()))){
            oneTimePrice.setFrequency(creationContainer.getFrequency());
            update=true;
        }

        if(creationContainer.getPricingScheme()!=null&&(oneTimePrice.getPricingScheme()==null||!creationContainer.getPricingScheme().getId().equals(oneTimePrice.getPricingScheme().getId()))){
            oneTimePrice.setPricingScheme(creationContainer.getPricingScheme());
            update=true;
        }

        return update;
    }

    public OneTimePrice updateOneTimePrice(OneTimePriceUpdate updateContainer,
                                           SecurityContextBase securityContext) {
        OneTimePrice oneTimePrice = updateContainer.getOneTimePrice();
        if (updateOneTimePriceNoMerge(oneTimePrice, updateContainer)) {
            repository.merge(oneTimePrice);
        }
        return oneTimePrice;
    }

    public void validate(OneTimePriceCreate creationContainer,
                         SecurityContextBase securityContext) {
        priceService.validate(creationContainer, securityContext);

        String frequencyId=creationContainer.getFrequencyId();
        Frequency frequency=frequencyId==null?null:getByIdOrNull(frequencyId,Frequency.class, SecuredBasic_.security,securityContext);
        if(frequency==null&&frequencyId!=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no frequency with id "+frequencyId);
        }
        creationContainer.setFrequency(frequency);


        String pricingSchemeId=creationContainer.getPricingSchemeId();
        PricingScheme pricingScheme=pricingSchemeId==null?null:getByIdOrNull(pricingSchemeId,PricingScheme.class, SecuredBasic_.security,securityContext);
        if(pricingScheme==null&&pricingSchemeId!=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no pricingScheme with id "+pricingSchemeId);
        }
        creationContainer.setPricingScheme(pricingScheme);
    }
}