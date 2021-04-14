package com.flexicore.billing.service;


import com.flexicore.billing.data.ContractRepository;
import com.flexicore.billing.model.Contract;
import com.flexicore.billing.request.ContractCreate;
import com.flexicore.billing.request.ContractFiltering;
import com.flexicore.billing.request.ContractUpdate;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;

@Extension
@Component

public class ContractService implements Plugin {

		@Autowired
	private ContractRepository repository;

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

	public void validateFiltering(ContractFiltering filtering,
								  SecurityContextBase securityContext) {
		basicService.validate(filtering, securityContext);
	}

	public PaginationResponse<Contract> getAllContracts(
			SecurityContextBase securityContext, ContractFiltering filtering) {
		List<Contract> list = listAllContracts(securityContext, filtering);
		long count = repository.countAllContracts(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public List<Contract> listAllContracts(SecurityContextBase securityContext, ContractFiltering filtering) {
		return repository.getAllContracts(securityContext, filtering);
	}

	public Contract createContract(ContractCreate creationContainer,
												 SecurityContextBase securityContext) {
		Contract contract = createContractNoMerge(creationContainer, securityContext);
		repository.merge(contract);
		return contract;
	}

	private Contract createContractNoMerge(ContractCreate creationContainer,
                                       SecurityContextBase securityContext) {
		Contract contract = new Contract();
		contract.setId(Baseclass.getBase64ID());

		updateContractNoMerge(contract, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(contract,securityContext);

		return contract;
	}

	private boolean updateContractNoMerge(Contract contract,
			ContractCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, contract);

		return update;
	}

	public Contract updateContract(ContractUpdate updateContainer,
												 SecurityContextBase securityContext) {
		Contract contract = updateContainer.getContract();
		if (updateContractNoMerge(contract, updateContainer)) {
			repository.merge(contract);
		}
		return contract;
	}

	public void validate(ContractCreate creationContainer,
                         SecurityContextBase securityContext) {
		basicService.validate(creationContainer, securityContext);
	}
}