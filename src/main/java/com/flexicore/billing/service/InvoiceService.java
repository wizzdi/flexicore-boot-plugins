package com.flexicore.billing.service;


import com.flexicore.billing.data.InvoiceRepository;
import com.flexicore.billing.model.Contract;
import com.flexicore.billing.model.Invoice;
import com.flexicore.billing.model.PaymentMethod;
import com.flexicore.billing.model.PaymentMethod_;
import com.flexicore.billing.request.InvoiceCreate;
import com.flexicore.billing.request.InvoiceFiltering;
import com.flexicore.billing.request.InvoiceUpdate;
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

public class InvoiceService implements Plugin {

		@Autowired
	private InvoiceRepository repository;

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

	public void validateFiltering(InvoiceFiltering filtering,
								  SecurityContextBase securityContext) {
		basicService.validate(filtering, securityContext);
		Set<String> paymentMethodIds = filtering.getPaymentMethodIds();
		Map<String, PaymentMethod> paymentMethodMap = paymentMethodIds.isEmpty() ? new HashMap<>() : listByIds(PaymentMethod.class, paymentMethodIds, PaymentMethod_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		paymentMethodIds.removeAll(paymentMethodMap.keySet());
		if (!paymentMethodIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No PaymentMethod with ids " + paymentMethodIds);
		}
		filtering.setPaymentMethods(new ArrayList<>(paymentMethodMap.values()));
	}

	public PaginationResponse<Invoice> getAllInvoices(
			SecurityContextBase securityContext, InvoiceFiltering filtering) {
		List<Invoice> list = repository.getAllInvoices(securityContext, filtering);
		long count = repository.countAllInvoices(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public Invoice createInvoice(InvoiceCreate creationContainer,
												 SecurityContextBase securityContext) {
		Invoice invoice = createInvoiceNoMerge(creationContainer, securityContext);
		repository.merge(invoice);
		return invoice;
	}

	private Invoice createInvoiceNoMerge(InvoiceCreate creationContainer,
                                       SecurityContextBase securityContext) {
		Invoice invoice = new Invoice();
		invoice.setId(Baseclass.getBase64ID());

		updateInvoiceNoMerge(invoice, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(invoice,securityContext);

		return invoice;
	}

	private boolean updateInvoiceNoMerge(Invoice invoice, InvoiceCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, invoice);
		if (creationContainer.getUsedPaymentMethod() != null && (invoice.getUsedPaymentMethod() == null || !creationContainer.getUsedPaymentMethod().getId().equals(invoice.getUsedPaymentMethod().getId()))) {
			invoice.setUsedPaymentMethod(creationContainer.getUsedPaymentMethod());
			update = true;
		}
		return update;
	}

	public Invoice updateInvoice(InvoiceUpdate updateContainer,
												 SecurityContextBase securityContext) {
		Invoice invoice = updateContainer.getInvoice();
		if (updateInvoiceNoMerge(invoice, updateContainer)) {
			repository.merge(invoice);
		}
		return invoice;
	}

	public void validate(InvoiceCreate creationContainer,
                         SecurityContextBase securityContext) {
		basicService.validate(creationContainer, securityContext);
		String usedPaymentMethodId = creationContainer.getUsedPaymentMethodId();
		PaymentMethod paymentMethod = usedPaymentMethodId == null ? null : getByIdOrNull(usedPaymentMethodId, PaymentMethod.class, null, securityContext);
		if (paymentMethod == null && usedPaymentMethodId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No PaymentMethod with id " + usedPaymentMethodId);
		}
		creationContainer.setUsedPaymentMethod(paymentMethod);
		
	}
}