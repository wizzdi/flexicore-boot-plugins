package com.flexicore.billing.service;


import com.flexicore.billing.data.ContractItemRepository;
import com.flexicore.billing.model.*;
import com.flexicore.billing.model.Currency;
import com.flexicore.billing.request.ContractItemCreate;
import com.flexicore.billing.request.ContractItemFiltering;
import com.flexicore.billing.request.ContractItemUpdate;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.model.Organization;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;



import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.SingularAttribute;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

	public void validateFiltering(ContractItemFiltering filtering, SecurityContextBase securityContext) {
		basicService.validate(filtering, securityContext);
		Set<String> contractIds = filtering.getContractIds();
		Map<String, Contract> contractMap = contractIds.isEmpty() ? new HashMap<>() : listByIds(Contract.class, contractIds, Contract_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		contractIds.removeAll(contractMap.keySet());
		if (!contractIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Contract with ids " + contractIds);
		}
		filtering.setContracts(new ArrayList<>(contractMap.values()));

		Set<String> businessServiceIds = filtering.getBusinessServiceIds();
		Map<String, BusinessService> businessServiceMap = businessServiceIds.isEmpty() ? new HashMap<>() : listByIds(BusinessService.class, businessServiceIds,BusinessService_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		businessServiceIds.removeAll(businessServiceMap.keySet());
		if (!businessServiceIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No BusinessService with ids " + businessServiceIds);
		}
		filtering.setBusinessServices(new ArrayList<>(businessServiceMap.values()));

		Set<String> priceListToServiceIds = filtering.getPriceListToServiceIds();
		Map<String, PriceListToService> priceListToServiceMap = priceListToServiceIds.isEmpty() ? new HashMap<>() : listByIds(PriceListToService.class, priceListToServiceIds,PriceListToService_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		priceListToServiceIds.removeAll(priceListToServiceMap.keySet());
		if (!priceListToServiceIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No PriceListToService with ids " + priceListToServiceIds);
		}
		filtering.setPriceListToService(new ArrayList<>(priceListToServiceMap.values()));
	}

	public PaginationResponse<ContractItem> getAllContractItems(
			SecurityContextBase securityContext, ContractItemFiltering filtering) {
		List<ContractItem> list = repository.getAllContractItems(securityContext, filtering);
		long count = repository.countAllContractItems(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public ContractItem createContractItem(ContractItemCreate creationContainer,
												 SecurityContextBase securityContext) {
		ContractItem contractItem = createContractItemNoMerge(creationContainer, securityContext);
		repository.merge(contractItem);
		return contractItem;
	}

	private ContractItem createContractItemNoMerge(ContractItemCreate creationContainer,
                                       SecurityContextBase securityContext) {
		ContractItem contractItem = new ContractItem();
		contractItem.setId(Baseclass.getBase64ID());

		updateContractItemNoMerge(contractItem, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(contractItem,securityContext);
		return contractItem;
	}

	private boolean updateContractItemNoMerge(ContractItem contractItem, ContractItemCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, contractItem);
		if (creationContainer.getBusinessService() != null && (contractItem.getBusinessService() == null || !creationContainer.getBusinessService().getId().equals(contractItem.getBusinessService().getId()))) {
			contractItem.setBusinessService(creationContainer.getBusinessService());
			update = true;
		}

		if (creationContainer.getContract() != null && (contractItem.getContract() == null || !creationContainer.getContract().getId().equals(contractItem.getContract().getId()))) {
			contractItem.setContract(creationContainer.getContract());
			update = true;
		}

		if (creationContainer.getCurrency() != null && (contractItem.getCurrency() == null || !creationContainer.getCurrency().getId().equals(contractItem.getCurrency().getId()))) {
			contractItem.setCurrency(creationContainer.getCurrency());
			update = true;
		}
		if (creationContainer.getPriceListToService() != null && (contractItem.getPriceListToService() == null || !creationContainer.getPriceListToService().getId().equals(contractItem.getPriceListToService().getId()))) {
			contractItem.setPriceListToService(creationContainer.getPriceListToService());
			update = true;
		}

		if (creationContainer.getPrice() != null && ( !creationContainer.getPrice().equals(contractItem.getPrice()))) {
			contractItem.setPrice(creationContainer.getPrice());
			update = true;
		}

		if (creationContainer.getValidFrom() != null && ( !creationContainer.getValidFrom().equals(contractItem.getValidFrom()))) {
			contractItem.setValidFrom(creationContainer.getValidFrom());
			update = true;
		}

		if (creationContainer.getValidTo() != null && ( !creationContainer.getValidTo().equals(contractItem.getValidTo()))) {
			contractItem.setValidTo(creationContainer.getValidTo());
			update = true;
		}
		return update;
	}

	public ContractItem updateContractItem(ContractItemUpdate updateContainer, SecurityContextBase securityContext) {
		ContractItem contractItem = updateContainer.getContractItem();
		if (updateContractItemNoMerge(contractItem, updateContainer)) {
			repository.merge(contractItem);
		}
		return contractItem;
	}

	public void validate(ContractItemCreate creationContainer, SecurityContextBase securityContext) {
		basicService.validate(creationContainer, securityContext);
		String contractId = creationContainer.getContractId();
		Contract contract = contractId == null ? null : getByIdOrNull(contractId, Contract.class, null, securityContext);
		if (contract == null && contractId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Contract with id " + contractId);
		}
		creationContainer.setContract(contract);

		String currencyId = creationContainer.getCurrencyId();
		Currency currency = currencyId == null ? null : getByIdOrNull(currencyId, Currency.class, null, securityContext);
		if (currency == null && currencyId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Currency with id " + currencyId);
		}
		creationContainer.setCurrency(currency);

		String businessServiceId = creationContainer.getBusinessServiceId();
		BusinessService businessService = businessServiceId == null ? null : getByIdOrNull(businessServiceId, BusinessService.class, null, securityContext);
		if (businessService == null && businessServiceId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No BusinessService with id " + businessServiceId);
		}
		creationContainer.setBusinessService(businessService);

		String priceListToServiceId = creationContainer.getPriceListToServiceId();
		PriceListToService priceListToService = priceListToServiceId == null ? null : getByIdOrNull(priceListToServiceId, PriceListToService.class, null, securityContext);
		if (priceListToService == null && priceListToServiceId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No PriceListToService with id " + priceListToServiceId);
		}
		creationContainer.setPriceListToService(priceListToService);
	}
}