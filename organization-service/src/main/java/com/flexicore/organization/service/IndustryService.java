package com.flexicore.organization.service;


import com.flexicore.model.Basic;
import com.flexicore.organization.model.Customer_;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.data.IndustryRepository;
import com.flexicore.organization.model.Customer;
import com.flexicore.organization.model.Industry;
import com.flexicore.organization.request.IndustryCreate;
import com.flexicore.organization.request.IndustryFiltering;
import com.flexicore.organization.request.IndustryUpdate;
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

public class IndustryService implements Plugin {


	@Autowired
	private IndustryRepository repository;

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

	public void validateFiltering(IndustryFiltering filtering,
			SecurityContext securityContext) {
		basicService.validate(filtering, securityContext);
		Set<String> customerIds = filtering.getCustomerIds();
		Map<String, Customer> customers = customerIds.isEmpty() ? new HashMap<>() : listByIds(Customer.class, customerIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		customerIds.removeAll(customers.keySet());
		if (!customerIds.isEmpty()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Organization with ids " + customerIds);
		}
		filtering.setCustomers(new ArrayList<>(customers.values()));
	}

	public PaginationResponse<Industry> getAllIndustries(SecurityContext securityContext, IndustryFiltering filtering) {
		List<Industry> list = repository.getAllIndustries(securityContext, filtering);
		long count = repository.countAllIndustries(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public Industry createIndustry(IndustryCreate creationContainer,
			SecurityContext securityContext) {
		Industry industry = createIndustryNoMerge(creationContainer, securityContext);
		repository.merge(industry);
		return industry;
	}

	private Industry createIndustryNoMerge(IndustryCreate creationContainer,
			SecurityContext securityContext) {
		Industry industry = new Industry();
		industry.setId(UUID.randomUUID().toString());
		updateIndustryNoMerge(industry, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(industry, securityContext);

		return industry;
	}

	private boolean updateIndustryNoMerge(Industry industry,
			IndustryCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, industry);

		return update;
	}

	public Industry updateIndustry(IndustryUpdate updateContainer,
			SecurityContext securityContext) {
		Industry industry = updateContainer.getIndustry();
		if (updateIndustryNoMerge(industry, updateContainer)) {
			repository.merge(industry);
		}
		return industry;
	}

	public void validate(IndustryCreate creationContainer,
			SecurityContext securityContext) {
		basicService.validate(creationContainer, securityContext);
	}
}
