package com.flexicore.organization.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.organization.data.CustomerRepository;
import com.flexicore.organization.model.Customer;
import com.flexicore.organization.request.CustomerCreate;
import com.flexicore.organization.request.CustomerFiltering;
import com.flexicore.organization.request.CustomerUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;

import java.util.List;
import java.util.Set;
import java.util.UUID;


@Extension
@Component

public class CustomerService implements Plugin {

	private static final Logger logger= LoggerFactory.getLogger(CustomerService.class);


	@Autowired
	private CustomerRepository repository;

	@Autowired
	private BasicService basicService;

	//TODO:update customer last login...
	/*@EventListener
	public void onUserLoggedIn(LoginEvent loginEvent){
		List<Customer> customers = listAllCustomers(null, new CustomerFiltering().setUsers(Collections.singletonList(loginEvent.getUser())));
		for (Customer customer : customers) {
			customer.setLastLogin(OffsetDateTime.now());
		}
		repository.massMerge(customers);
		if(logger.isDebugEnabled()&&!customers.isEmpty()){
			logger.debug("updated last used for customers: "+customers.stream().map(f->f.getName()+"("+f.getId()+")").collect(Collectors.joining(",")));
		}
	}*/



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

	public void validateFiltering(CustomerFiltering filtering,
			SecurityContext securityContext) {
		basicService.validate(filtering, securityContext);

	}

	public PaginationResponse<Customer> getAllCustomers(
			SecurityContext securityContext, CustomerFiltering filtering) {
		List<Customer> list = listAllCustomers(securityContext, filtering);
		long count = repository.countAllCustomers(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public List<Customer> listAllCustomers(SecurityContext securityContext, CustomerFiltering filtering) {
		return repository.getAllCustomers(securityContext, filtering);
	}

	public Customer createCustomer(CustomerCreate creationContainer,
			SecurityContext securityContext) {
		Customer customer = createCustomerNoMerge(creationContainer, securityContext);
		repository.merge(customer);
		return customer;
	}

	public Customer createCustomerNoMerge(CustomerCreate creationContainer,
			SecurityContext securityContext) {
		Customer customer = new Customer();
		customer.setId(UUID.randomUUID().toString());
		updateCustomerNoMerge(customer, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(customer, securityContext);
		return customer;
	}

	public boolean updateCustomerNoMerge(Customer customer,
			CustomerCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, customer);
		if(creationContainer.getExternalId()!=null && !creationContainer.getExternalId().equals(customer.getExternalId())){
			customer.setExternalId(creationContainer.getExternalId());
			update=true;
		}

		return update;
	}

	public Customer updateCustomer(CustomerUpdate updateContainer,
			SecurityContext securityContext) {
		Customer customer = updateContainer.getCustomer();
		if (updateCustomerNoMerge(customer, updateContainer)) {
			repository.merge(customer);
		}
		return customer;
	}

	public void validate(CustomerCreate creationContainer,
			SecurityContext securityContext) {
		basicService.validate(creationContainer, securityContext);

	}
}
