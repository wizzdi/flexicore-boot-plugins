package com.flexicore.billing.service;


import com.flexicore.billing.data.InvoiceItemRepository;
import com.flexicore.billing.model.*;
import com.flexicore.billing.request.InvoiceItemCreate;
import com.flexicore.billing.request.InvoiceItemFiltering;
import com.flexicore.billing.request.InvoiceItemUpdate;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class InvoiceItemService implements Plugin {

		@Autowired
	private InvoiceItemRepository repository;

	@Autowired
	private BasicService basicService;

public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
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

	public void validateFiltering(InvoiceItemFiltering filtering,
								  SecurityContextBase securityContext) {
		basicService.validate(filtering, securityContext);
		Set<String> invoiceIds = filtering.getInvoiceIds();
		Map<String, Invoice> invoiceMap = invoiceIds.isEmpty() ? new HashMap<>() : listByIds(Invoice.class, invoiceIds,Invoice_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		invoiceIds.removeAll(invoiceMap.keySet());
		if (!invoiceIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Invoice with ids " + invoiceIds);
		}
		filtering.setInvoices(new ArrayList<>(invoiceMap.values()));

		Set<String> contractItemIds = filtering.getContractItemIds();
		Map<String, ContractItem> contractItemMap = contractItemIds.isEmpty() ? new HashMap<>() : listByIds(ContractItem.class, contractItemIds,ContractItem_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		contractItemIds.removeAll(contractItemMap.keySet());
		if (!contractItemIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No ContractItem with ids " + contractItemIds);
		}
		filtering.setContractItems(new ArrayList<>(contractItemMap.values()));

	}

	public PaginationResponse<InvoiceItem> getAllInvoiceItems(
			SecurityContextBase securityContext, InvoiceItemFiltering filtering) {
		List<InvoiceItem> list = repository.getAllInvoiceItems(securityContext, filtering);
		long count = repository.countAllInvoiceItems(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public InvoiceItem createInvoiceItem(InvoiceItemCreate creationContainer,
												 SecurityContextBase securityContext) {
		InvoiceItem invoiceItem = createInvoiceItemNoMerge(creationContainer, securityContext);
		repository.merge(invoiceItem);
		return invoiceItem;
	}

	private InvoiceItem createInvoiceItemNoMerge(InvoiceItemCreate creationContainer,
                                       SecurityContextBase securityContext) {
		InvoiceItem invoiceItem = new InvoiceItem();
		invoiceItem.setId(Baseclass.getBase64ID());

		updateInvoiceItemNoMerge(invoiceItem, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(invoiceItem,securityContext);

		return invoiceItem;
	}

	private boolean updateInvoiceItemNoMerge(InvoiceItem invoiceItem,
			InvoiceItemCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, invoiceItem);
		if (creationContainer.getContractItem() != null && (invoiceItem.getContractItem() == null || !creationContainer.getContractItem().getId().equals(invoiceItem.getContractItem().getId()))) {
			invoiceItem.setContractItem(creationContainer.getContractItem());
			update = true;
		}
		if (creationContainer.getInvoice() != null && (invoiceItem.getInvoice() == null || !creationContainer.getInvoice().getId().equals(invoiceItem.getInvoice().getId()))) {
			invoiceItem.setInvoice(creationContainer.getInvoice());
			update = true;
		}
		return update;
	}

	public InvoiceItem updateInvoiceItem(InvoiceItemUpdate updateContainer,
												 SecurityContextBase securityContext) {
		InvoiceItem invoiceItem = updateContainer.getInvoiceItem();
		if (updateInvoiceItemNoMerge(invoiceItem, updateContainer)) {
			repository.merge(invoiceItem);
		}
		return invoiceItem;
	}

	public void validate(InvoiceItemCreate creationContainer, SecurityContextBase securityContext) {
		basicService.validate(creationContainer, securityContext);

		String contractItemId = creationContainer.getContractItemId();
		ContractItem contractItem = contractItemId == null ? null : getByIdOrNull(contractItemId, ContractItem.class, null, securityContext);
		if (contractItem == null && contractItemId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No ContractItem with id " + contractItemId);
		}
		creationContainer.setContractItem(contractItem);

		String invoiceId = creationContainer.getInvoiceId();
		Invoice invoice = invoiceId == null ? null : getByIdOrNull(invoiceId, Invoice.class, null, securityContext);
		if (invoice == null && invoiceId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Invoice with id " + invoiceId);
		}
		creationContainer.setInvoice(invoice);

	}
}