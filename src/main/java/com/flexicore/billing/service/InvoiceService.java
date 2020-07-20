package com.flexicore.billing.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.data.InvoiceRepository;
import com.flexicore.billing.model.Contract;
import com.flexicore.billing.model.Invoice;
import com.flexicore.billing.model.PaymentMethod;
import com.flexicore.billing.request.InvoiceCreate;
import com.flexicore.billing.request.InvoiceFiltering;
import com.flexicore.billing.request.InvoiceUpdate;
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
public class InvoiceService implements ServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private InvoiceRepository repository;

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

	public void validateFiltering(InvoiceFiltering filtering,
								  SecurityContext securityContext) {
		baseclassNewService.validateFilter(filtering, securityContext);
		Set<String> paymentMethodIds = filtering.getPaymentMethodIds();
		Map<String, PaymentMethod> paymentMethodMap = paymentMethodIds.isEmpty() ? new HashMap<>() : listByIds(PaymentMethod.class, paymentMethodIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		paymentMethodIds.removeAll(paymentMethodMap.keySet());
		if (!paymentMethodIds.isEmpty()) {
			throw new BadRequestException("No PaymentMethod with ids " + paymentMethodIds);
		}
		filtering.setPaymentMethods(new ArrayList<>(paymentMethodMap.values()));
	}

	public PaginationResponse<Invoice> getAllInvoices(
			SecurityContext securityContext, InvoiceFiltering filtering) {
		List<Invoice> list = repository.getAllInvoices(securityContext, filtering);
		long count = repository.countAllInvoices(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public Invoice createInvoice(InvoiceCreate creationContainer,
												 SecurityContext securityContext) {
		Invoice invoice = createInvoiceNoMerge(creationContainer, securityContext);
		repository.merge(invoice);
		return invoice;
	}

	private Invoice createInvoiceNoMerge(InvoiceCreate creationContainer,
                                       SecurityContext securityContext) {
		Invoice invoice = new Invoice(creationContainer.getName(),securityContext);
		updateInvoiceNoMerge(invoice, creationContainer);
		return invoice;
	}

	private boolean updateInvoiceNoMerge(Invoice invoice, InvoiceCreate creationContainer) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(creationContainer, invoice);
		if (creationContainer.getUsedPaymentMethod() != null && (invoice.getUsedPaymentMethod() == null || !creationContainer.getUsedPaymentMethod().getId().equals(invoice.getUsedPaymentMethod().getId()))) {
			invoice.setUsedPaymentMethod(creationContainer.getUsedPaymentMethod());
			update = true;
		}
		return update;
	}

	public Invoice updateInvoice(InvoiceUpdate updateContainer,
												 SecurityContext securityContext) {
		Invoice invoice = updateContainer.getInvoice();
		if (updateInvoiceNoMerge(invoice, updateContainer)) {
			repository.merge(invoice);
		}
		return invoice;
	}

	public void validate(InvoiceCreate creationContainer,
                         SecurityContext securityContext) {
		baseclassNewService.validate(creationContainer, securityContext);
		String usedPaymentMethodId = creationContainer.getUsedPaymentMethodId();
		PaymentMethod paymentMethod = usedPaymentMethodId == null ? null : getByIdOrNull(usedPaymentMethodId, PaymentMethod.class, null, securityContext);
		if (paymentMethod == null && usedPaymentMethodId != null) {
			throw new BadRequestException("No PaymentMethod with id " + usedPaymentMethodId);
		}
		creationContainer.setUsedPaymentMethod(paymentMethod);
		
	}
}