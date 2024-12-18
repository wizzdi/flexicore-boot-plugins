package com.flexicore.organization.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.organization.data.SupplierRepository;
import com.flexicore.organization.model.Supplier;
import com.flexicore.organization.model.SupplierApi;
import com.flexicore.organization.request.SupplierCreate;
import com.flexicore.organization.request.SupplierFiltering;
import com.flexicore.organization.request.SupplierUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class SupplierService implements Plugin {

	private static final Logger logger = LoggerFactory.getLogger(SupplierService.class);

	@Autowired
	private SupplierRepository repository;
	@Autowired
	private OrganizationService organizationService;




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

	public PaginationResponse<Supplier> listAllSuppliers(
			SecurityContext securityContext, SupplierFiltering filtering) {

		List<Supplier> endpoints = repository.getAllSuppliers(securityContext,
				filtering);
		long count = repository.countAllSuppliers(securityContext, filtering);
		return new PaginationResponse<>(endpoints, filtering, count);
	}


	public List<Supplier> getAllSuppliers(SecurityContext securityContext,
										  SupplierFiltering filtering) {
		return repository.getAllSuppliers(securityContext, filtering);
	}


	public Supplier createSupplierNoMerge(SupplierCreate creationContainer,
										  SecurityContext securityContext) {
		Supplier supplier = new Supplier();
		supplier.setId(UUID.randomUUID().toString());

		updateSupplierNoMerge(creationContainer, supplier);
		BaseclassService.createSecurityObjectNoMerge(supplier, securityContext);

		return supplier;

	}

	public Supplier updateSupplier(SupplierUpdate creationContainer,
								   SecurityContext securityContext) {
		Supplier Supplier = creationContainer.getSupplier();
		if (updateSupplierNoMerge(creationContainer, Supplier)) {
			repository.merge(Supplier);
		}
		return Supplier;
	}


	public boolean updateSupplierNoMerge(SupplierCreate create,
										 Supplier supplier) {
		boolean update = organizationService.updateOrganizationNoMerge(supplier, create);

		if (create.getSupplierApi() != null && !(supplier.getSupplierApi() == null || create.getSupplierApi().getId().equals(supplier.getSupplierApi().getId()))) {
			supplier.setSupplierApi(create.getSupplierApi());
			update = true;
		}

		return update;
	}

	public void validateFiltering(SupplierFiltering filtering,
								  SecurityContext securityContext) {
		organizationService.validateFiltering(filtering,securityContext);


	}


	public void validateCreate(SupplierCreate creationContainer,
							   SecurityContext securityContext) {
		organizationService.validate(creationContainer,securityContext);
		if (creationContainer.getName() == null
				|| creationContainer.getName().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Supplier name must be non empty and non null");
		}
		SupplierFiltering supplierFiltering = new SupplierFiltering();
		supplierFiltering.setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike(creationContainer.getName()));
		List<Supplier> suppliers = getAllSuppliers(securityContext, supplierFiltering);
		if (!suppliers.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier with name " + creationContainer.getName() + " already exists");
		}
		String supplierApiId = creationContainer.getSupplierApiId();
		SupplierApi supplierApi = supplierApiId != null ? getByIdOrNull(supplierApiId, SupplierApi.class, securityContext) : null;
		if (supplierApi == null && supplierApiId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Supplier api id " + supplierApiId);
		}
		creationContainer.setSupplierApi(supplierApi);

	}

	public void validateUpdate(SupplierUpdate creationContainer,
							   SecurityContext securityContext) {

		if (creationContainer.getName() != null) {
			SupplierFiltering supplierFiltering = new SupplierFiltering();
			supplierFiltering.setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike(creationContainer.getName()));
			List<Supplier> suppliers = getAllSuppliers(securityContext,
					supplierFiltering)
					.parallelStream()
					.filter(f -> !f.getId().equals(
							creationContainer.getSupplier().getId()))
					.collect(Collectors.toList());
			if (!suppliers.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier with name "
						+ creationContainer.getName() + " already exists");
			}
		}
		String supplierApiId = creationContainer.getSupplierApiId();
		SupplierApi supplierApi = supplierApiId != null ? getByIdOrNull(
				supplierApiId, SupplierApi.class,  securityContext) : null;
		if (supplierApi == null && supplierApiId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Supplier api id " + supplierApiId);
		}
		creationContainer.setSupplierApi(supplierApi);

	}

	public Supplier createSupplier(SupplierCreate creationContainer,
								   SecurityContext securityContext) {
		Supplier Supplier = createSupplierNoMerge(creationContainer,
				securityContext);
		repository.merge(Supplier);
		return Supplier;
	}


}
