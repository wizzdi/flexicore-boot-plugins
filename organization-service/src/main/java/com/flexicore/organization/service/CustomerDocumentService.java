package com.flexicore.organization.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;

import com.flexicore.organization.data.CustomerDocumentRepository;
import com.flexicore.organization.model.Customer;
import com.flexicore.organization.model.CustomerDocument;
import com.flexicore.organization.request.CustomerDocumentCreate;
import com.flexicore.organization.request.CustomerDocumentFiltering;
import com.flexicore.organization.request.CustomerDocumentUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.file.model.FileResource;
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
import java.util.stream.Collectors;


@Extension
@Component

public class CustomerDocumentService implements Plugin {


	@Autowired
	private CustomerDocumentRepository repository;

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

	public void validateFiltering(CustomerDocumentFiltering filtering,
			SecurityContext securityContext) {
		basicService.validate(filtering, securityContext);
		Set<String> customerIds = filtering.getCustomerIds();
		Map<String, Customer> customers = customerIds.isEmpty() ? new HashMap<>() : listByIds(Customer.class, customerIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		customerIds.removeAll(customers.keySet());
		if (!customerIds.isEmpty()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Organization with ids " + customerIds);
		}
		filtering.setCustomers(new ArrayList<>(customers.values()));
	}

	public PaginationResponse<CustomerDocument> getAllCustomerDocuments(SecurityContext securityContext, CustomerDocumentFiltering filtering) {
		List<CustomerDocument> list = repository.getAllCustomerDocuments(securityContext, filtering);
		long count = repository.countAllCustomerDocuments(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public CustomerDocument createCustomerDocument(CustomerDocumentCreate creationContainer,
			SecurityContext securityContext) {
		CustomerDocument customerDocument = createCustomerDocumentNoMerge(creationContainer, securityContext);
		repository.merge(customerDocument);
		return customerDocument;
	}

	private CustomerDocument createCustomerDocumentNoMerge(CustomerDocumentCreate creationContainer,
			SecurityContext securityContext) {
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setId(UUID.randomUUID().toString());
		updateCustomerDocumentNoMerge(customerDocument, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(customerDocument, securityContext);

		return customerDocument;
	}

	private boolean updateCustomerDocumentNoMerge(CustomerDocument customerDocument,
			CustomerDocumentCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, customerDocument);

		if (creationContainer.getCustomer() != null && (customerDocument.getCustomer() == null || !creationContainer.getCustomer().getId().equals(customerDocument.getCustomer().getId()))) {
			customerDocument.setCustomer(creationContainer.getCustomer());
			update = true;
		}

		if (creationContainer.getFileResource() != null && (customerDocument.getFileResource() == null || !creationContainer.getFileResource().getId().equals(customerDocument.getFileResource().getId()))) {
			customerDocument.setFileResource(creationContainer.getFileResource());
			update = true;
		}
		return update;
	}

	public CustomerDocument updateCustomerDocument(CustomerDocumentUpdate updateContainer,
			SecurityContext securityContext) {
		CustomerDocument customerDocument = updateContainer.getCustomerDocument();
		if (updateCustomerDocumentNoMerge(customerDocument, updateContainer)) {
			repository.merge(customerDocument);
		}
		return customerDocument;
	}

	public void validate(CustomerDocumentCreate creationContainer,
			SecurityContext securityContext) {
		basicService.validate(creationContainer, securityContext);
		String customerId = creationContainer.getCustomerId();
		Customer customer = customerId == null ? null : getByIdOrNull(customerId, Customer.class, securityContext);
		if (customer == null && customerId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Customer with id " + customerId);
		}
		creationContainer.setCustomer(customer);

		String fileResourceId = creationContainer.getFileResourceId();
		FileResource fileResource = fileResourceId == null ? null : getByIdOrNull(fileResourceId, FileResource.class, securityContext);
		if (fileResource == null && fileResourceId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No FileResource with id " + fileResourceId);
		}
		creationContainer.setFileResource(fileResource);
	}
}
