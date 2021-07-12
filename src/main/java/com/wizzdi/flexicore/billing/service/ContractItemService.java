package com.wizzdi.flexicore.billing.service;


import com.wizzdi.flexicore.billing.data.ContractItemRepository;
import com.flexicore.model.SecuredBasic_;
import com.wizzdi.flexicore.billing.model.billing.Charge;
import com.wizzdi.flexicore.billing.model.billing.Charge_;
import com.wizzdi.flexicore.contract.model.*;
import com.wizzdi.flexicore.billing.request.ContractItemCreate;
import com.wizzdi.flexicore.billing.request.ContractItemFiltering;
import com.wizzdi.flexicore.billing.request.ContractItemUpdate;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.pricing.model.price.PriceListItem;
import com.wizzdi.flexicore.pricing.model.price.RecurringPrice;
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

public class ContractItemService implements Plugin {

    @Autowired
    private ContractItemRepository repository;

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

    public void validateFiltering(ContractItemFiltering filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);
        Set<String> contractIds = filtering.getContractIds();
        Map<String, Contract> contractMap = contractIds.isEmpty() ? new HashMap<>() : listByIds(Contract.class, contractIds, Contract_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        contractIds.removeAll(contractMap.keySet());
        if (!contractIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Contract with ids " + contractIds);
        }
        filtering.setContracts(new ArrayList<>(contractMap.values()));

        Set<String> priceListsItemsIds = filtering.getPriceListsItemsIds();
        Map<String, PriceListItem> chargeMap = priceListsItemsIds.isEmpty() ? new HashMap<>() : listByIds(PriceListItem.class, priceListsItemsIds, Charge_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        priceListsItemsIds.removeAll(chargeMap.keySet());
        if (!priceListsItemsIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PriceListItem with ids " + priceListsItemsIds);
        }
        filtering.setPriceListItems(new ArrayList<>(chargeMap.values()));

    }

    public PaginationResponse<ContractItem> getAllContractItems(
            SecurityContextBase securityContext, ContractItemFiltering filtering) {
        List<ContractItem> list = listAllContractItems(securityContext, filtering);
        long count = repository.countAllContractItems(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<ContractItem> listAllContractItems(SecurityContextBase securityContext, ContractItemFiltering filtering) {
        return repository.getAllContractItems(securityContext, filtering);
    }

    public ContractItem createContractItem(ContractItemCreate contractItemCreate,
                                         SecurityContextBase securityContext) {
        ContractItem contractItem = createContractItemNoMerge(contractItemCreate, securityContext);
        repository.merge(contractItem);
        return contractItem;
    }

    public ContractItem createContractItemNoMerge(ContractItemCreate contractItemCreate,
                                                SecurityContextBase securityContext) {
        ContractItem contractItem = new ContractItem();
        contractItem.setId(Baseclass.getBase64ID());

        updateContractItemNoMerge(contractItem, contractItemCreate);
        BaseclassService.createSecurityObjectNoMerge(contractItem, securityContext);

        return contractItem;
    }

    private boolean updateContractItemNoMerge(ContractItem contractItem,
                                             ContractItemCreate contractItemCreate) {
        boolean update = basicService.updateBasicNoMerge(contractItemCreate, contractItem);
        if (contractItemCreate.getContract() != null && (contractItem.getContract() == null || !contractItemCreate.getContract().getId().equals(contractItem.getContract().getId()))) {
            contractItem.setContract(contractItemCreate.getContract());
            update = true;
        }
        if (contractItemCreate.getPriceListItem() != null && (contractItem.getPriceListItem() == null || !contractItemCreate.getPriceListItem().getId().equals(contractItem.getPriceListItem().getId()))) {
            contractItem.setPriceListItem(contractItemCreate.getPriceListItem());
            update = true;
        }
        if (contractItemCreate.getRecurringPrice() != null && (contractItem.getRecurringPrice() == null || !contractItemCreate.getRecurringPrice().getId().equals(contractItem.getRecurringPrice().getId()))) {
            contractItem.setRecurringPrice(contractItemCreate.getRecurringPrice());
            update = true;
        }
        if (contractItemCreate.getExternalId() != null && !contractItemCreate.getExternalId().equals(contractItem.getExternalId())) {
            contractItem.setExternalId(contractItemCreate.getExternalId());
            update = true;
        }
        if (contractItemCreate.getValidFrom() != null && !contractItemCreate.getValidFrom().equals(contractItem.getValidFrom())) {
            contractItem.setValidFrom(contractItemCreate.getValidFrom());
            update = true;
        }
        if (contractItemCreate.getValidTo() != null && !contractItemCreate.getValidTo().equals(contractItem.getValidTo())) {
            contractItem.setValidTo(contractItemCreate.getValidTo());
            update = true;
        }
        return update;
    }

    public ContractItem updateContractItem(ContractItemUpdate contractItemUpdate,
                                         SecurityContextBase securityContext) {
        ContractItem contractItem = contractItemUpdate.getContractItem();
        if (updateContractItemNoMerge(contractItem, contractItemUpdate)) {
            repository.merge(contractItem);
        }
        return contractItem;
    }

    public void validate(ContractItemCreate contractItemCreate, SecurityContextBase securityContext) {
        basicService.validate(contractItemCreate, securityContext);

        String contractId = contractItemCreate.getContractId();
        Contract contract = contractId == null ? null : getByIdOrNull(contractId, Contract.class, SecuredBasic_.security, securityContext);

        if (contract == null && contractId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Contract with id " + contractId);
        }
        contractItemCreate.setContract(contract);

        String recurringPriceId = contractItemCreate.getRecurringPriceId();
        RecurringPrice recurringPrice = recurringPriceId == null ? null : getByIdOrNull(recurringPriceId, RecurringPrice.class, SecuredBasic_.security, securityContext);
        if (recurringPrice == null && recurringPriceId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No RecurringPrice with id " + recurringPriceId);
        }
        contractItemCreate.setRecurringPrice(recurringPrice);

        String priceListItemId = contractItemCreate.getPriceListItemId();
        PriceListItem priceListItem = priceListItemId == null ? null : getByIdOrNull(priceListItemId, PriceListItem.class, SecuredBasic_.security, securityContext);
        if (priceListItem == null && priceListItemId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PriceListItem with id " + priceListItemId);
        }
        contractItemCreate.setPriceListItem(priceListItem);


    }
}