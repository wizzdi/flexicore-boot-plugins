package com.flexicore.organization.service;


import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.data.IndividualCustomerRepository;
import com.flexicore.organization.model.IndividualCustomer;
import com.flexicore.organization.request.IndividualCustomerCreate;
import com.flexicore.organization.request.IndividualCustomerFiltering;
import com.flexicore.organization.request.IndividualCustomerUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.service.BaseclassService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Extension
@Component

public class IndividualCustomerService implements Plugin {


	@Autowired
	private IndividualCustomerRepository repository;


	@Autowired
	private CustomerService customerService;





	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}


	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
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

	public void validateFiltering(IndividualCustomerFiltering filtering,
			SecurityContext securityContext) {
		customerService.validateFiltering(filtering, securityContext);

	}

	public PaginationResponse<IndividualCustomer> getAllIndividualCustomers(
			SecurityContext securityContext, IndividualCustomerFiltering filtering) {
		List<IndividualCustomer> list = repository.getAllIndividualCustomers(securityContext, filtering);
		long count = repository.countAllIndividualCustomers(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public IndividualCustomer createIndividualCustomer(IndividualCustomerCreate creationContainer,
			SecurityContext securityContext) {
		IndividualCustomer individualCustomer = createIndividualCustomerNoMerge(creationContainer, securityContext);
		repository.merge(individualCustomer);
		return individualCustomer;
	}

	public IndividualCustomer createIndividualCustomerNoMerge(IndividualCustomerCreate creationContainer,
			SecurityContext securityContext) {
		IndividualCustomer individualCustomer = new IndividualCustomer();
		individualCustomer.setId(UUID.randomUUID().toString());
		updateIndividualCustomerNoMerge(individualCustomer, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(individualCustomer, securityContext);
		return individualCustomer;
	}

	public boolean updateIndividualCustomerNoMerge(IndividualCustomer individualCustomer,
			IndividualCustomerCreate creationContainer) {
		boolean update = customerService.updateCustomerNoMerge(individualCustomer,
				creationContainer);


		return update;
	}

	public IndividualCustomer updateIndividualCustomer(IndividualCustomerUpdate updateContainer,
			SecurityContext securityContext) {
		IndividualCustomer individualCustomer = updateContainer.getIndividualCustomer();
		if (updateIndividualCustomerNoMerge(individualCustomer, updateContainer)) {
			repository.merge(individualCustomer);
		}
		return individualCustomer;
	}

	public void validate(IndividualCustomerCreate creationContainer,
			SecurityContext securityContext) {
		customerService.validate(creationContainer, securityContext);

	}
}
