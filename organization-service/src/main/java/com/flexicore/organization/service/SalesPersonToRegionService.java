package com.flexicore.organization.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.Address_;
import com.flexicore.organization.data.SalesPersonToRegionRepository;
import com.flexicore.organization.model.SalesPerson;
import com.flexicore.organization.model.SalesPersonToRegion;
import com.flexicore.organization.model.SalesPerson_;
import com.flexicore.organization.model.SalesRegion;
import com.flexicore.organization.request.SalesPersonToRegionCreate;
import com.flexicore.organization.request.SalesPersonToRegionFiltering;
import com.flexicore.organization.request.SalesPersonToRegionUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
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


@Extension
@Component

public class SalesPersonToRegionService implements Plugin {


	@Autowired
	private SalesPersonToRegionRepository repository;
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


	public void validateFiltering(SalesPersonToRegionFiltering filtering,
								  SecurityContext securityContext) {
		basicService.validate(filtering, securityContext);

	}


	public PaginationResponse<SalesPersonToRegion> getAllSalesPersonToRegions(
			SecurityContext securityContext, SalesPersonToRegionFiltering filtering) {
		List<SalesPersonToRegion> list = listAllSalesPersonToRegions(securityContext, filtering);
		long count = repository.countAllSalesPersonToRegions(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}


	public List<SalesPersonToRegion> listAllSalesPersonToRegions(SecurityContext securityContext,
								   SalesPersonToRegionFiltering filtering) {
		return repository.getAllSalesPersonToRegions(securityContext, filtering);
	}

	public SalesPersonToRegion createSalesPersonToRegion(SalesPersonToRegionCreate creationContainer,
						   SecurityContext securityContext) {
		SalesPersonToRegion salesPersonToRegion = createSalesPersonToRegionNoMerge(creationContainer, securityContext);
		repository.merge(salesPersonToRegion);
		return salesPersonToRegion;
	}


	public SalesPersonToRegion createSalesPersonToRegionNoMerge(SalesPersonToRegionCreate creationContainer,
								  SecurityContext securityContext) {
		SalesPersonToRegion salesPersonToRegion = new SalesPersonToRegion();
		salesPersonToRegion.setId(UUID.randomUUID().toString());
		updateSalesPersonToRegionNoMerge(salesPersonToRegion, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(salesPersonToRegion, securityContext);

		return salesPersonToRegion;
	}


	public boolean updateSalesPersonToRegionNoMerge(SalesPersonToRegion salesPersonToRegion, SalesPersonToRegionCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, salesPersonToRegion);

		if (creationContainer.getSalesPerson() != null && (salesPersonToRegion.getSalesPerson() == null || !creationContainer.getSalesPerson().getId().equals(salesPersonToRegion.getSalesPerson().getId()))) {
			salesPersonToRegion.setSalesPerson(creationContainer.getSalesPerson());
			update = true;
		}
		if (creationContainer.getSalesRegion() != null && (salesPersonToRegion.getSalesRegion() == null || !creationContainer.getSalesRegion().getId().equals(salesPersonToRegion.getSalesRegion().getId()))) {
			salesPersonToRegion.setSalesRegion(creationContainer.getSalesRegion());
			update = true;
		}
		return update;
	}

	public SalesPersonToRegion updateSalesPersonToRegion(SalesPersonToRegionUpdate updateContainer, SecurityContext securityContext) {
		SalesPersonToRegion salesPersonToRegion = updateContainer.getSalesPersonToRegion();
		if (updateSalesPersonToRegionNoMerge(salesPersonToRegion, updateContainer)) {
			repository.merge(salesPersonToRegion);
		}
		return salesPersonToRegion;
	}


	public void validate(SalesPersonToRegionCreate creationContainer, SecurityContext securityContext) {
		basicService.validate(creationContainer,securityContext);
		String salesPersonId = creationContainer.getSalesPersonId();
		SalesPerson salesPerson = salesPersonId == null ? null : getByIdOrNull(salesPersonId,
				SalesPerson.class,  securityContext);
		if (salesPerson == null && salesPersonId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No SalesPerson with id " + salesPersonId);
		}
		creationContainer.setSalesPerson(salesPerson);

		String salesRegionId = creationContainer.getSalesRegionId();
		SalesRegion salesRegion = salesRegionId == null ? null : getByIdOrNull(salesRegionId,
				SalesRegion.class,  securityContext);
		if (salesRegion == null && salesRegionId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No SalesRegion with id " + salesRegionId);
		}
		creationContainer.setSalesRegion(salesRegion);
	}


}
