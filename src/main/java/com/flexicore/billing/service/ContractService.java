package com.flexicore.billing.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.data.ContractRepository;
import com.flexicore.billing.model.Contract;
import com.flexicore.billing.request.ContractCreate;
import com.flexicore.billing.request.ContractFiltering;
import com.flexicore.billing.request.ContractUpdate;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@PluginInfo(version = 1)
@Extension
@Component
@Primary
public class ContractService implements ServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private ContractRepository repository;

	@Autowired
	private BaseclassNewService baseclassNewService;

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
                                                 List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids,
                                                   SecurityContext securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}

	public void validateFiltering(ContractFiltering filtering,
								  SecurityContext securityContext) {
		baseclassNewService.validateFilter(filtering, securityContext);
	}

	public PaginationResponse<Contract> getAllContracts(
			SecurityContext securityContext, ContractFiltering filtering) {
		List<Contract> list = repository.getAllContracts(securityContext, filtering);
		long count = repository.countAllContracts(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public Contract createContract(ContractCreate creationContainer,
												 SecurityContext securityContext) {
		Contract contract = createContractNoMerge(creationContainer, securityContext);
		repository.merge(contract);
		return contract;
	}

	private Contract createContractNoMerge(ContractCreate creationContainer,
                                       SecurityContext securityContext) {
		Contract contract = new Contract(creationContainer.getName(),securityContext);
		updateContractNoMerge(contract, creationContainer);
		return contract;
	}

	private boolean updateContractNoMerge(Contract contract,
			ContractCreate creationContainer) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(creationContainer, contract);

		return update;
	}

	public Contract updateContract(ContractUpdate updateContainer,
												 SecurityContext securityContext) {
		Contract contract = updateContainer.getContract();
		if (updateContractNoMerge(contract, updateContainer)) {
			repository.merge(contract);
		}
		return contract;
	}

	public void validate(ContractCreate creationContainer,
                         SecurityContext securityContext) {
		baseclassNewService.validate(creationContainer, securityContext);
	}
}