package com.flexicore.organization.service;


import com.flexicore.model.Basic;
import com.flexicore.organization.model.Customer_;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.data.IndustryToCustomerRepository;
import com.flexicore.organization.model.Customer;
import com.flexicore.organization.model.Industry;
import com.flexicore.organization.model.IndustryToCustomer;
import com.flexicore.organization.request.IndustryToCustomerCreate;
import com.flexicore.organization.request.IndustryToCustomerFiltering;
import com.flexicore.organization.request.IndustryToCustomerUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
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

public class IndustryToCustomerService implements Plugin {


	@Autowired
	private IndustryToCustomerRepository repository;

	@Autowired
	private BasicService basicService;





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

	public void validateFiltering(IndustryToCustomerFiltering filtering,
			SecurityContext securityContext) {
		basicService.validate(filtering, securityContext);
		Set<String> customerIds = filtering.getCustomerIds();
		Map<String, Customer> customers = customerIds.isEmpty() ? new HashMap<>() : listByIds(Customer.class, customerIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		customerIds.removeAll(customers.keySet());
		if (!customerIds.isEmpty()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Organization with ids " + customerIds);
		}
		filtering.setCustomers(new ArrayList<>(customers.values()));
	}

	public PaginationResponse<IndustryToCustomer> getAllIndustryToCustomers(SecurityContext securityContext, IndustryToCustomerFiltering filtering) {
		List<IndustryToCustomer> list = repository.getAllIndustryToCustomers(securityContext, filtering);
		long count = repository.countAllIndustryToCustomers(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public IndustryToCustomer createIndustryToCustomer(IndustryToCustomerCreate creationContainer,
			SecurityContext securityContext) {
		IndustryToCustomer industryToCustomer = createIndustryToCustomerNoMerge(creationContainer, securityContext);
		repository.merge(industryToCustomer);
		return industryToCustomer;
	}

	public IndustryToCustomer createIndustryToCustomerNoMerge(IndustryToCustomerCreate creationContainer,
			SecurityContext securityContext) {
		IndustryToCustomer industryToCustomer = new IndustryToCustomer();
		industryToCustomer.setId(UUID.randomUUID().toString());
		updateIndustryToCustomerNoMerge(industryToCustomer, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(industryToCustomer, securityContext);

		return industryToCustomer;
	}

	private boolean updateIndustryToCustomerNoMerge(IndustryToCustomer industryToCustomer,
			IndustryToCustomerCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, industryToCustomer);

		if (creationContainer.getCustomer() != null && (industryToCustomer.getCustomer() == null || !creationContainer.getCustomer().getId().equals(industryToCustomer.getCustomer().getId()))) {
			industryToCustomer.setCustomer(creationContainer.getCustomer());
			update = true;
		}

		if (creationContainer.getIndustry() != null && (industryToCustomer.getIndustry() == null || !creationContainer.getIndustry().getId().equals(industryToCustomer.getIndustry().getId()))) {
			industryToCustomer.setIndustry(creationContainer.getIndustry());
			update = true;
		}
		return update;
	}

	public IndustryToCustomer updateIndustryToCustomer(IndustryToCustomerUpdate updateContainer,
			SecurityContext securityContext) {
		IndustryToCustomer industryToCustomer = updateContainer.getIndustryToCustomer();
		if (updateIndustryToCustomerNoMerge(industryToCustomer, updateContainer)) {
			repository.merge(industryToCustomer);
		}
		return industryToCustomer;
	}

	public void validate(IndustryToCustomerCreate creationContainer,
			SecurityContext securityContext) {
		basicService.validate(creationContainer, securityContext);
		String customerId = creationContainer.getCustomerId();
		Customer customer = customerId == null ? null : getByIdOrNull(customerId, Customer.class,  securityContext);
		if (customer == null && customerId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Customer with id " + customerId);
		}
		creationContainer.setCustomer(customer);

		String industryId = creationContainer.getIndustryId();
		Industry industry = industryId == null ? null : getByIdOrNull(industryId, Industry.class,  securityContext);
		if (industry == null && industryId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Industry with id " + industryId);
		}
		creationContainer.setIndustry(industry);
	}
}
