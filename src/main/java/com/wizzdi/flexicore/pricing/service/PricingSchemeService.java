package com.wizzdi.flexicore.pricing.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.data.PricingSchemeRepository;
import com.wizzdi.flexicore.pricing.model.price.Frequency;
import com.wizzdi.flexicore.pricing.model.price.Money;
import com.wizzdi.flexicore.pricing.model.price.PricingScheme;
import com.wizzdi.flexicore.pricing.request.PricingSchemeCreate;
import com.wizzdi.flexicore.pricing.request.PricingSchemeFiltering;
import com.wizzdi.flexicore.pricing.request.PricingSchemeUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;

@Extension
@Component

public class PricingSchemeService implements Plugin {

    @Autowired
    private PricingSchemeRepository repository;

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

    public void validateFiltering(PricingSchemeFiltering filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);
    }

    public PaginationResponse<PricingScheme> getAllPricingSchemes(
            SecurityContextBase securityContext, PricingSchemeFiltering filtering) {
        List<PricingScheme> list = listAllPricingSchemes(securityContext, filtering);
        long count = repository.countAllPricingSchemes(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

	public List<PricingScheme> listAllPricingSchemes(SecurityContextBase securityContext, PricingSchemeFiltering filtering) {
		return repository.listAllPricingSchemes(securityContext, filtering);
	}

	public PricingScheme createPricingScheme(PricingSchemeCreate creationContainer,
                                                 SecurityContextBase securityContext) {
        PricingScheme pricingScheme = createPricingSchemeNoMerge(creationContainer, securityContext);
        repository.merge(pricingScheme);
        return pricingScheme;
    }

    public PricingScheme createPricingSchemeNoMerge(PricingSchemeCreate creationContainer,
                                                         SecurityContextBase securityContext) {
        PricingScheme pricingScheme = new PricingScheme();
        pricingScheme.setId(Baseclass.getBase64ID());
        updatePricingSchemeNoMerge(pricingScheme, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(pricingScheme, securityContext);
        return pricingScheme;
    }

    public boolean updatePricingSchemeNoMerge(PricingScheme pricingScheme,
                                                 PricingSchemeCreate creationContainer) {
        boolean update = basicService.updateBasicNoMerge(creationContainer, pricingScheme);
        if(creationContainer.getPricingModel()!=null&&!creationContainer.getPricingModel().equals(pricingScheme.getPricingModel())){
            pricingScheme.setPricingModel(creationContainer.getPricingModel());
            update=true;
        }

        if(creationContainer.getFixedPrice()!=null&&(pricingScheme.getFixedPrice()==null||!creationContainer.getFixedPrice().getId().equals(pricingScheme.getFixedPrice().getId()))){
            pricingScheme.setFixedPrice(creationContainer.getFixedPrice());
            update=true;
        }
        return update;
    }

    public PricingScheme updatePricingScheme(PricingSchemeUpdate updateContainer,
                                                 SecurityContextBase securityContext) {
        PricingScheme pricingScheme = updateContainer.getPricingScheme();
        if (updatePricingSchemeNoMerge(pricingScheme, updateContainer)) {
            repository.merge(pricingScheme);
        }
        return pricingScheme;
    }

    public void validate(PricingSchemeCreate creationContainer,
                         SecurityContextBase securityContext) {
        basicService.validate(creationContainer, securityContext);

        String fixedPriceId=creationContainer.getFixedPriceId();
        Money money=fixedPriceId==null?null:getByIdOrNull(fixedPriceId,Money.class, SecuredBasic_.security,securityContext);
        if(money==null&&fixedPriceId!=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no money with id "+fixedPriceId);
        }
        creationContainer.setFixedPrice(money);


    }
}