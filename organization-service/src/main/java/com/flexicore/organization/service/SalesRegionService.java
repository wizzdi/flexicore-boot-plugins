package com.flexicore.organization.service;


import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.data.SalesRegionRepository;
import com.flexicore.organization.model.SalesRegion;
import com.flexicore.organization.request.SalesRegionCreate;
import com.flexicore.organization.request.SalesRegionFiltering;
import com.flexicore.organization.request.SalesRegionUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;


@Extension
@Component

public class SalesRegionService implements Plugin {


	@Autowired
	private SalesRegionRepository repository;

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

	public PaginationResponse<SalesRegion> getAllSalesRegions(
			SecurityContext securityContext, SalesRegionFiltering filtering) {

		List<SalesRegion> endpoints = repository.listAllSalesRegions(securityContext, filtering);
		long count = repository.countAllSalesRegions(securityContext, filtering);
		return new PaginationResponse<>(endpoints, filtering, count);
	}

	public SalesRegion createSalesRegion(SalesRegionCreate creationContainer,
			SecurityContext securityContext) {
		SalesRegion salesRegion = createSalesRegionNoMerge(creationContainer,
				securityContext);
		repository.merge(salesRegion);
		return salesRegion;
	}

	public SalesRegion createSalesRegionNoMerge(
			SalesRegionCreate creationContainer, SecurityContext securityContext) {
		SalesRegion salesRegion = new SalesRegion();
		salesRegion.setId(UUID.randomUUID().toString());

		updateSalesRegionNoMerge(salesRegion, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(salesRegion, securityContext);

		return salesRegion;

	}

	public SalesRegion updateSalesRegion(SalesRegionUpdate creationContainer,
			SecurityContext securityContext) {
		SalesRegion salesRegion = creationContainer.getSalesRegion();
		if (updateSalesRegionNoMerge(salesRegion, creationContainer)) {
			repository.merge(salesRegion);
		}
		return salesRegion;
	}

	public boolean updateSalesRegionNoMerge(SalesRegion salesRegion,
			SalesRegionCreate salesRegionCreate) {
		boolean update = basicService.updateBasicNoMerge(salesRegionCreate,salesRegion);

		return update;
	}

	public void validateFiltering(SalesRegionFiltering filtering,
			SecurityContext securityContext) {
		basicService.validate(filtering,securityContext);

	}

}
