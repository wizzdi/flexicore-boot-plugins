package com.flexicore.organization.service;


import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.data.OrganizationalCustomerRepository;
import com.flexicore.organization.model.OrganizationalCustomer;
import com.flexicore.organization.request.OrganizationalCustomerCreate;
import com.flexicore.organization.request.OrganizationalCustomerFiltering;
import com.flexicore.organization.request.OrganizationalCustomerUpdate;
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

public class OrganizationalCustomerService implements Plugin {


	@Autowired
	private OrganizationalCustomerRepository repository;


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

	public void validateFiltering(OrganizationalCustomerFiltering filtering,
			SecurityContext securityContext) {
		customerService.validateFiltering(filtering, securityContext);

	}

	public PaginationResponse<OrganizationalCustomer> getAllOrganizationalCustomers(
			SecurityContext securityContext, OrganizationalCustomerFiltering filtering) {
		List<OrganizationalCustomer> list = listAllOrganizationalCustomers(securityContext, filtering);
		long count = repository.countAllOrganizationalCustomers(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public List<OrganizationalCustomer> listAllOrganizationalCustomers(SecurityContext securityContext, OrganizationalCustomerFiltering filtering) {
		return repository.getAllOrganizationalCustomers(securityContext, filtering);
	}

	public OrganizationalCustomer createOrganizationalCustomer(OrganizationalCustomerCreate creationContainer,
			SecurityContext securityContext) {
		OrganizationalCustomer organizationalCustomer = createOrganizationalCustomerNoMerge(creationContainer, securityContext);
		repository.merge(organizationalCustomer);
		return organizationalCustomer;
	}

	public OrganizationalCustomer createOrganizationalCustomerNoMerge(OrganizationalCustomerCreate creationContainer,
			SecurityContext securityContext) {
		OrganizationalCustomer organizationalCustomer = new OrganizationalCustomer();
		organizationalCustomer.setId(UUID.randomUUID().toString());

		updateOrganizationalCustomerNoMerge(organizationalCustomer, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(organizationalCustomer, securityContext);
		return organizationalCustomer;
	}

	public boolean updateOrganizationalCustomerNoMerge(OrganizationalCustomer organizationalCustomer,
			OrganizationalCustomerCreate creationContainer) {
		boolean update = customerService.updateCustomerNoMerge(organizationalCustomer,
				creationContainer);

		if(creationContainer.getOrganization()!=null && (organizationalCustomer.getOrganization()==null||!creationContainer.getOrganization().getId().equals(organizationalCustomer.getOrganization().getId()))){
			organizationalCustomer.setOrganization(creationContainer.getOrganization());
			update=true;
		}


		return update;
	}

	public OrganizationalCustomer updateOrganizationalCustomer(OrganizationalCustomerUpdate updateContainer,
			SecurityContext securityContext) {
		OrganizationalCustomer organizationalCustomer = updateContainer.getOrganizationalCustomer();
		if (updateOrganizationalCustomerNoMerge(organizationalCustomer, updateContainer)) {
			repository.merge(organizationalCustomer);
		}
		return organizationalCustomer;
	}

	public void validate(OrganizationalCustomerCreate creationContainer,
			SecurityContext securityContext) {
		customerService.validate(creationContainer, securityContext);

	}
}
