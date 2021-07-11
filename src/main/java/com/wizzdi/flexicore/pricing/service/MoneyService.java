package com.wizzdi.flexicore.pricing.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.data.MoneyRepository;
import com.wizzdi.flexicore.pricing.model.price.Currency;
import com.wizzdi.flexicore.pricing.model.price.Money;
import com.wizzdi.flexicore.pricing.request.MoneyCreate;
import com.wizzdi.flexicore.pricing.request.MoneyFiltering;
import com.wizzdi.flexicore.pricing.request.MoneyUpdate;
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
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class MoneyService implements Plugin {

    @Autowired
    private MoneyRepository repository;

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

    public void validateFiltering(MoneyFiltering filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);
        Set<String> currenciesIds=filtering.getCurrenciesIds();
        Map<String,Currency> currencyMap=currenciesIds.isEmpty()?new HashMap<>():listByIds(Currency.class,currenciesIds,SecuredBasic_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(),f->f));
        currenciesIds.removeAll(currencyMap.keySet());
        if(!currenciesIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no currencies with ids "+currenciesIds);
        }
        filtering.setCurrencies(new ArrayList<>(currencyMap.values()));
    }

    public PaginationResponse<Money> getAllMoney(
            SecurityContextBase securityContext, MoneyFiltering filtering) {
        List<Money> list = listAllMoney(securityContext, filtering);
        long count = repository.countAllMoney(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Money> listAllMoney(SecurityContextBase securityContext, MoneyFiltering filtering) {
        return repository.getAllMoney(securityContext, filtering);
    }

    public Money createMoney(MoneyCreate creationContainer,
                             SecurityContextBase securityContext) {
        Money money = createMoneyNoMerge(creationContainer, securityContext);
        repository.merge(money);
        return money;
    }

    private Money createMoneyNoMerge(MoneyCreate creationContainer,
                                     SecurityContextBase securityContext) {
        Money money = new Money();
        money.setId(Baseclass.getBase64ID());

        updateMoneyNoMerge(money, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(money, securityContext);
        return money;
    }

    private boolean updateMoneyNoMerge(Money money,
                                       MoneyCreate creationContainer) {
        boolean update = basicService.updateBasicNoMerge(creationContainer, money);
        if (creationContainer.getCurrency() != null && (money.getCurrency() == null || !creationContainer.getCurrency().getId().equals(money.getCurrency().getId()))) {
            money.setCurrency(creationContainer.getCurrency());
            update = true;
        }
        if (creationContainer.getCents() != null && !creationContainer.getCents().equals(money.getCents())) {
            money.setCents(creationContainer.getCents());
            update = true;
        }


        return update;
    }

    public Money updateMoney(MoneyUpdate updateContainer,
                             SecurityContextBase securityContext) {
        Money money = updateContainer.getMoney();
        if (updateMoneyNoMerge(money, updateContainer)) {
            repository.merge(money);
        }
        return money;
    }

    public void validate(MoneyCreate creationContainer,
                         SecurityContextBase securityContext) {
        basicService.validate(creationContainer, securityContext);
        String currencyId=creationContainer.getCurrencyId();
        Currency currency=currencyId==null?null:getByIdOrNull(currencyId,Currency.class, SecuredBasic_.security,securityContext);
        if(currency==null&&currencyId!=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no currency with id "+currencyId);
        }
        creationContainer.setCurrency(currency);
    }
}