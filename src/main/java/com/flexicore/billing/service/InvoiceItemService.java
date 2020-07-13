package com.flexicore.billing.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.data.InvoiceItemRepository;
import com.flexicore.billing.model.*;
import com.flexicore.billing.request.InvoiceItemCreate;
import com.flexicore.billing.request.InvoiceItemFiltering;
import com.flexicore.billing.request.InvoiceItemUpdate;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
@Primary
public class InvoiceItemService implements ServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private InvoiceItemRepository repository;

	@PluginInfo(version = 1)
	@Autowired
	private BaseclassNewService baseclassNewService;

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
                                                 List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids,
                                                   SecurityContext securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}

	public void validateFiltering(InvoiceItemFiltering filtering,
								  SecurityContext securityContext) {
		baseclassNewService.validateFilter(filtering, securityContext);
		Set<String> invoiceIds = filtering.getInvoiceIds();
		Map<String, Invoice> invoiceMap = invoiceIds.isEmpty() ? new HashMap<>() : listByIds(Invoice.class, invoiceIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		invoiceIds.removeAll(invoiceMap.keySet());
		if (!invoiceIds.isEmpty()) {
			throw new BadRequestException("No Invoice with ids " + invoiceIds);
		}
		filtering.setInvoices(new ArrayList<>(invoiceMap.values()));

		Set<String> contractItemIds = filtering.getContractItemIds();
		Map<String, ContractItem> contractItemMap = contractItemIds.isEmpty() ? new HashMap<>() : listByIds(ContractItem.class, contractItemIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		contractItemIds.removeAll(contractItemMap.keySet());
		if (!contractItemIds.isEmpty()) {
			throw new BadRequestException("No ContractItem with ids " + contractItemIds);
		}
		filtering.setContractItems(new ArrayList<>(contractItemMap.values()));

	}

	public PaginationResponse<InvoiceItem> getAllInvoiceItems(
			SecurityContext securityContext, InvoiceItemFiltering filtering) {
		List<InvoiceItem> list = repository.getAllInvoiceItems(securityContext, filtering);
		long count = repository.countAllInvoiceItems(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public InvoiceItem createInvoiceItem(InvoiceItemCreate creationContainer,
												 SecurityContext securityContext) {
		InvoiceItem invoiceItem = createInvoiceItemNoMerge(creationContainer, securityContext);
		repository.merge(invoiceItem);
		return invoiceItem;
	}

	private InvoiceItem createInvoiceItemNoMerge(InvoiceItemCreate creationContainer,
                                       SecurityContext securityContext) {
		InvoiceItem invoiceItem = new InvoiceItem(creationContainer.getName(),securityContext);
		updateInvoiceItemNoMerge(invoiceItem, creationContainer);
		return invoiceItem;
	}

	private boolean updateInvoiceItemNoMerge(InvoiceItem invoiceItem,
			InvoiceItemCreate creationContainer) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(creationContainer, invoiceItem);
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
												 SecurityContext securityContext) {
		InvoiceItem invoiceItem = updateContainer.getInvoiceItem();
		if (updateInvoiceItemNoMerge(invoiceItem, updateContainer)) {
			repository.merge(invoiceItem);
		}
		return invoiceItem;
	}

	public void validate(InvoiceItemCreate creationContainer, SecurityContext securityContext) {
		baseclassNewService.validate(creationContainer, securityContext);

		String contractItemId = creationContainer.getContractItemId();
		ContractItem contractItem = contractItemId == null ? null : getByIdOrNull(contractItemId, ContractItem.class, null, securityContext);
		if (contractItem == null && contractItemId != null) {
			throw new BadRequestException("No ContractItem with id " + contractItemId);
		}
		creationContainer.setContractItem(contractItem);

		String invoiceId = creationContainer.getInvoiceId();
		Invoice invoice = invoiceId == null ? null : getByIdOrNull(invoiceId, Invoice.class, null, securityContext);
		if (invoice == null && invoiceId != null) {
			throw new BadRequestException("No Invoice with id " + invoiceId);
		}
		creationContainer.setInvoice(invoice);

	}
}