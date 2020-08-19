package com.flexicore.billing.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.data.ContractItemRepository;
import com.flexicore.billing.model.BusinessService;
import com.flexicore.billing.model.Contract;
import com.flexicore.billing.model.ContractItem;
import com.flexicore.billing.model.Currency;
import com.flexicore.billing.model.PriceListToService;
import com.flexicore.billing.request.ContractItemCreate;
import com.flexicore.billing.request.ContractItemFiltering;
import com.flexicore.billing.request.ContractItemUpdate;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.model.Organization;
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
public class ContractItemService implements ServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private ContractItemRepository repository;

	@Autowired
	private BaseclassNewService baseclassNewService;

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}

	public void validateFiltering(ContractItemFiltering filtering, SecurityContext securityContext) {
		baseclassNewService.validateFilter(filtering, securityContext);
		Set<String> contractIds = filtering.getContractIds();
		Map<String, Contract> contractMap = contractIds.isEmpty() ? new HashMap<>() : listByIds(Contract.class, contractIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		contractIds.removeAll(contractMap.keySet());
		if (!contractIds.isEmpty()) {
			throw new BadRequestException("No Contract with ids " + contractIds);
		}
		filtering.setContracts(new ArrayList<>(contractMap.values()));

		Set<String> businessServiceIds = filtering.getBusinessServiceIds();
		Map<String, BusinessService> businessServiceMap = businessServiceIds.isEmpty() ? new HashMap<>() : listByIds(BusinessService.class, businessServiceIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		businessServiceIds.removeAll(businessServiceMap.keySet());
		if (!businessServiceIds.isEmpty()) {
			throw new BadRequestException("No BusinessService with ids " + businessServiceIds);
		}
		filtering.setBusinessServices(new ArrayList<>(businessServiceMap.values()));

		Set<String> priceListToServiceIds = filtering.getPriceListToServiceIds();
		Map<String, PriceListToService> priceListToServiceMap = priceListToServiceIds.isEmpty() ? new HashMap<>() : listByIds(PriceListToService.class, priceListToServiceIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		priceListToServiceIds.removeAll(priceListToServiceMap.keySet());
		if (!priceListToServiceIds.isEmpty()) {
			throw new BadRequestException("No PriceListToService with ids " + priceListToServiceIds);
		}
		filtering.setPriceListToService(new ArrayList<>(priceListToServiceMap.values()));
	}

	public PaginationResponse<ContractItem> getAllContractItems(
			SecurityContext securityContext, ContractItemFiltering filtering) {
		List<ContractItem> list = repository.getAllContractItems(securityContext, filtering);
		long count = repository.countAllContractItems(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public ContractItem createContractItem(ContractItemCreate creationContainer,
												 SecurityContext securityContext) {
		ContractItem contractItem = createContractItemNoMerge(creationContainer, securityContext);
		repository.merge(contractItem);
		return contractItem;
	}

	private ContractItem createContractItemNoMerge(ContractItemCreate creationContainer,
                                       SecurityContext securityContext) {
		ContractItem contractItem = new ContractItem(creationContainer.getName(),securityContext);
		updateContractItemNoMerge(contractItem, creationContainer);
		return contractItem;
	}

	private boolean updateContractItemNoMerge(ContractItem contractItem, ContractItemCreate creationContainer) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(creationContainer, contractItem);
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

	public ContractItem updateContractItem(ContractItemUpdate updateContainer, SecurityContext securityContext) {
		ContractItem contractItem = updateContainer.getContractItem();
		if (updateContractItemNoMerge(contractItem, updateContainer)) {
			repository.merge(contractItem);
		}
		return contractItem;
	}

	public void validate(ContractItemCreate creationContainer, SecurityContext securityContext) {
		baseclassNewService.validate(creationContainer, securityContext);
		String contractId = creationContainer.getContractId();
		Contract contract = contractId == null ? null : getByIdOrNull(contractId, Contract.class, null, securityContext);
		if (contract == null && contractId != null) {
			throw new BadRequestException("No Contract with id " + contractId);
		}
		creationContainer.setContract(contract);

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

		String priceListToServiceId = creationContainer.getPriceListToServiceId();
		PriceListToService priceListToService = priceListToServiceId == null ? null : getByIdOrNull(priceListToServiceId, PriceListToService.class, null, securityContext);
		if (priceListToService == null && priceListToServiceId != null) {
			throw new BadRequestException("No PriceListToService with id " + priceListToServiceId);
		}
		creationContainer.setPriceListToService(priceListToService);
	}
}