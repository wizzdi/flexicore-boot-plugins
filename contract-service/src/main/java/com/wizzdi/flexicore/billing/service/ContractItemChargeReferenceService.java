package com.wizzdi.flexicore.billing.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.billing.data.ContractItemChargeReferenceRepository;
import com.wizzdi.flexicore.billing.model.billing.Charge_;
import com.wizzdi.flexicore.billing.request.ContractItemChargeReferenceCreate;
import com.wizzdi.flexicore.billing.request.ContractItemChargeReferenceFiltering;
import com.wizzdi.flexicore.billing.request.ContractItemChargeReferenceUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.contract.model.ContractItem;
import com.wizzdi.flexicore.contract.model.ContractItemChargeReference;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class ContractItemChargeReferenceService implements Plugin {

    @Autowired
    private ContractItemChargeReferenceRepository repository;

    @Autowired
    private ChargeReferenceService chargeReferenceService;

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

    public void validateFiltering(ContractItemChargeReferenceFiltering filtering,
                                  SecurityContextBase securityContext) {
        chargeReferenceService.validateFiltering(filtering, securityContext);
        Set<String> contractItemsIds = filtering.getContractItemsIds();
        Map<String, ContractItem> contractItemMap = contractItemsIds.isEmpty() ? new HashMap<>() : listByIds(ContractItem.class, contractItemsIds, Charge_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        contractItemsIds.removeAll(contractItemMap.keySet());
        if (!contractItemsIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No ContractItem with ids " + contractItemsIds);
        }
        filtering.setContractItems(new ArrayList<>(contractItemMap.values()));


    }

    public PaginationResponse<ContractItemChargeReference> getAllContractItemChargeReferences(
            SecurityContextBase securityContext, ContractItemChargeReferenceFiltering filtering) {
        List<ContractItemChargeReference> list = listAllContractItemChargeReferences(securityContext, filtering);
        long count = repository.countAllContractItemChargeReferences(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

	public List<ContractItemChargeReference> listAllContractItemChargeReferences(SecurityContextBase securityContext, ContractItemChargeReferenceFiltering filtering) {
		return repository.getAllContractItemChargeReferences(securityContext, filtering);
	}

	public ContractItemChargeReference createContractItemChargeReference(ContractItemChargeReferenceCreate contractItemChargeReferenceCreate,
                                 SecurityContextBase securityContext) {
        ContractItemChargeReference contractItemChargeReference = createContractItemChargeReferenceNoMerge(contractItemChargeReferenceCreate, securityContext);
        repository.merge(contractItemChargeReference);
        return contractItemChargeReference;
    }

    public ContractItemChargeReference createContractItemChargeReferenceNoMerge(ContractItemChargeReferenceCreate contractItemChargeReferenceCreate,
                                        SecurityContextBase securityContext) {
        ContractItemChargeReference contractItemChargeReference = new ContractItemChargeReference();
        contractItemChargeReference.setId(Baseclass.getBase64ID());

        updateContractItemChargeReferenceNoMerge(contractItemChargeReference, contractItemChargeReferenceCreate);
        BaseclassService.createSecurityObjectNoMerge(contractItemChargeReference, securityContext);

        return contractItemChargeReference;
    }

    private boolean updateContractItemChargeReferenceNoMerge(ContractItemChargeReference contractItemChargeReference, ContractItemChargeReferenceCreate contractItemChargeReferenceCreate) {
        boolean update = chargeReferenceService.updateChargeReferenceNoMerge(contractItemChargeReference, contractItemChargeReferenceCreate);
        if (contractItemChargeReferenceCreate.getContractItem() != null && (contractItemChargeReference.getContractItem() == null || !contractItemChargeReferenceCreate.getContractItem().getId().equals(contractItemChargeReference.getContractItem().getId()))) {
            contractItemChargeReference.setContractItem(contractItemChargeReferenceCreate.getContractItem());
            update = true;
        }





        return update;
    }

    public ContractItemChargeReference updateContractItemChargeReference(ContractItemChargeReferenceUpdate contractItemChargeReferenceUpdate,
                                 SecurityContextBase securityContext) {
        ContractItemChargeReference contractItemChargeReference = contractItemChargeReferenceUpdate.getContractItemChargeReference();
        if (updateContractItemChargeReferenceNoMerge(contractItemChargeReference, contractItemChargeReferenceUpdate)) {
            repository.merge(contractItemChargeReference);
        }
        return contractItemChargeReference;
    }

    public void validate(ContractItemChargeReferenceCreate contractItemChargeReferenceCreate,
                         SecurityContextBase securityContext) {
        chargeReferenceService.validate(contractItemChargeReferenceCreate, securityContext);
        String contractItemId = contractItemChargeReferenceCreate.getContractItemId();
        ContractItem contractItem = contractItemId == null ? null : getByIdOrNull(contractItemId, ContractItem.class, SecuredBasic_.security, securityContext);
        if (contractItem == null && contractItemId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No ContractItem with id " + contractItemId);
        }
        contractItemChargeReferenceCreate.setContractItem(contractItem);


    }
}