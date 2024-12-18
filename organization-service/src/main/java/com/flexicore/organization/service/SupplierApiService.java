package com.flexicore.organization.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.organization.data.SupplierApiRepository;
import com.flexicore.organization.model.SupplierApi;
import com.flexicore.organization.request.SupplierApiCreate;
import com.flexicore.organization.request.SupplierApiFiltering;
import com.flexicore.organization.request.SupplierApiUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Extension
@Component

public class SupplierApiService implements Plugin {


	@Autowired
	private SupplierApiRepository repository;
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

	public PaginationResponse<SupplierApi> listAllSupplierApis(
			SecurityContext securityContext, SupplierApiFiltering filtering) {

		List<SupplierApi> endpoints = repository.getAllSupplierApis(
				securityContext, filtering);
		long count = repository
				.countAllSupplierApis(securityContext, filtering);
		return new PaginationResponse<>(endpoints, filtering, count);
	}

	
	public List<SupplierApi> getAllSupplierApis(
			SecurityContext securityContext, SupplierApiFiltering filtering) {
		return repository.getAllSupplierApis(securityContext, filtering);
	}

	
	public SupplierApi createSupplierApi(SupplierApiCreate creationContainer,
			SecurityContext securityContext) {
		SupplierApi SupplierApi = createSupplierApiNoMerge(creationContainer,
				securityContext);
		repository.merge(SupplierApi);
		return SupplierApi;
	}

	
	public SupplierApi createSupplierApiNoMerge(
			SupplierApiCreate creationContainer, SecurityContext securityContext) {
		SupplierApi supplierApi = new SupplierApi();
		supplierApi.setId(UUID.randomUUID().toString());
		updateSupplierApiNoMerge(creationContainer, supplierApi);
		BaseclassService.createSecurityObjectNoMerge(supplierApi, securityContext);

		return supplierApi;

	}

	
	public SupplierApi updateSupplierApi(SupplierApiUpdate creationContainer,
			SecurityContext securityContext) {
		SupplierApi SupplierApi = creationContainer.getSupplierApi();
		if (updateSupplierApiNoMerge(creationContainer, SupplierApi)) {
			repository.merge(SupplierApi);
		}
		return SupplierApi;
	}

	
	public boolean updateSupplierApiNoMerge(SupplierApiCreate create,
			SupplierApi supplierApi) {
		boolean update =basicService.updateBasicNoMerge(create,supplierApi);

		if (create.getImplementorCanonicalName() != null && !create.getImplementorCanonicalName().equals(supplierApi.getImplementorCanonicalName())) {
			supplierApi.setImplementorCanonicalName(create.getImplementorCanonicalName());
			update = true;
		}

		return update;
	}

	public void validateFiltering(SupplierApiFiltering filtering,
			SecurityContext securityContext) {
		basicService.validate(filtering,securityContext);

	}

	
	public void validateCreate(SupplierApiCreate creationContainer,
			SecurityContext securityContext) {
		if (creationContainer.getName() == null
				|| creationContainer.getName().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"SupplierApi name must be non empty and non null");
		}
		SupplierApiFiltering supplierApiFiltering = new SupplierApiFiltering();
		supplierApiFiltering.setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike(creationContainer.getName()));
		List<SupplierApi> supplierApis = getAllSupplierApis(securityContext,
				supplierApiFiltering);
		if (!supplierApis.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"SupplierApi with name "
					+ creationContainer.getName() + " already exists");
		}

	}

	
	public void validateUpdate(SupplierApiUpdate creationContainer,
			SecurityContext securityContext) {
		basicService.validate(creationContainer,securityContext);

		if (creationContainer.getName() != null) {
			SupplierApiFiltering supplierApiFiltering = new SupplierApiFiltering();
			supplierApiFiltering.setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike(creationContainer.getName()));
			List<SupplierApi> supplierApis = getAllSupplierApis(
					securityContext, supplierApiFiltering)
					.parallelStream()
					.filter(f -> !f.getId().equals(
							creationContainer.getSupplierApi().getId()))
					.collect(Collectors.toList());
			if (!supplierApis.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"SupplierApi with name "
						+ creationContainer.getName() + " already exists");
			}
		}

	}

}
