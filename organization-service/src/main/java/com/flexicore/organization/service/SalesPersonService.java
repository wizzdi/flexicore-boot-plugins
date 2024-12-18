package com.flexicore.organization.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.organization.data.SalesPersonRepository;
import com.flexicore.organization.model.SalesPerson;
import com.flexicore.organization.model.SalesPerson_;
import com.flexicore.organization.model.SalesRegion;
import com.flexicore.organization.request.SalesPersonCreate;
import com.flexicore.organization.request.SalesPersonFiltering;
import com.flexicore.organization.request.SalesPersonUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
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

public class SalesPersonService implements Plugin {


	@Autowired
	private SalesPersonRepository repository;


	@Autowired
	private EmployeeService employeeService;






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

	public PaginationResponse<SalesPerson> getAllSalesPeople(
			SecurityContext securityContext, SalesPersonFiltering filtering) {

		List<SalesPerson> endpoints = repository.listAllSalesPeople(securityContext, filtering);
		long count = repository.countAllSalesPeople(securityContext, filtering);
		return new PaginationResponse<>(endpoints, filtering, count);
	}

	public SalesPerson createSalesPerson(SalesPersonCreate creationContainer,
			SecurityContext securityContext) {

		SalesPerson salesPerson = createSalesPersonNoMerge(creationContainer, securityContext);

		repository.merge(salesPerson);
		return salesPerson;
	}

	public SalesPerson createSalesPersonNoMerge(
			SalesPersonCreate creationContainer, SecurityContext securityContext) {
		SalesPerson salesPerson = new SalesPerson();
		salesPerson.setId(UUID.randomUUID().toString());

		updateSalesPersonNoMerge(salesPerson, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(salesPerson, securityContext);

		return salesPerson;

	}

	public SalesPerson updateSalesPerson(SalesPersonUpdate creationContainer,
			SecurityContext securityContext) {
		SalesPerson salesPerson = creationContainer.getSalesPerson();
		if (updateSalesPersonNoMerge(salesPerson, creationContainer)) {
			repository.merge(salesPerson);
		}
		return salesPerson;
	}

	public boolean updateSalesPersonNoMerge(SalesPerson salesPerson,
			SalesPersonCreate salesPersonCreate) {
		return employeeService.updateEmployeeNoMerge(salesPerson, salesPersonCreate);
	}

	public void validateFiltering(SalesPersonFiltering filtering,
			SecurityContext securityContext) {
		employeeService.validateFiltering(filtering,securityContext);
		Set<String> regionIds = filtering.getRegionIds();
		Map<String, SalesRegion> salesRegion = regionIds.isEmpty() ? new HashMap<>() : listByIds(SalesRegion.class, regionIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		regionIds.removeAll(salesRegion.keySet());
		if (!salesRegion.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Sales Region with ids " + regionIds);
		}
		filtering.setSalesRegions(new ArrayList<>(salesRegion.values()));
	}



}
