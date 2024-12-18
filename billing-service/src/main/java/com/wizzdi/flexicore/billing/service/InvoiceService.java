package com.wizzdi.flexicore.billing.service;


import com.wizzdi.flexicore.billing.data.InvoiceRepository;
import com.wizzdi.flexicore.billing.model.payment.Invoice;
import com.wizzdi.flexicore.billing.request.InvoiceCreate;
import com.wizzdi.flexicore.billing.request.InvoiceFiltering;
import com.wizzdi.flexicore.billing.request.InvoiceUpdate;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;

@Extension
@Component

public class InvoiceService implements Plugin {

    @Autowired
    private InvoiceRepository repository;

    @Autowired
    private BasicService basicService;

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
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

    public void validateFiltering(InvoiceFiltering filtering,
                                  SecurityContext securityContext) {
        basicService.validate(filtering, securityContext);

    }

    public PaginationResponse<Invoice> getAllInvoices(
            SecurityContext securityContext, InvoiceFiltering filtering) {
        List<Invoice> list = listAllInvoices(securityContext, filtering);
        long count = repository.countAllInvoices(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

	public List<Invoice> listAllInvoices(SecurityContext securityContext, InvoiceFiltering filtering) {
		return repository.getAllInvoices(securityContext, filtering);
	}

	public Invoice createInvoice(InvoiceCreate invoiceCreate,
                                 SecurityContext securityContext) {
        Invoice invoice = createInvoiceNoMerge(invoiceCreate, securityContext);
        repository.merge(invoice);
        return invoice;
    }

    public Invoice createInvoiceNoMerge(InvoiceCreate invoiceCreate,
                                        SecurityContext securityContext) {
        Invoice invoice = new Invoice();
        invoice.setId(UUID.randomUUID().toString());

        updateInvoiceNoMerge(invoice, invoiceCreate);
        BaseclassService.createSecurityObjectNoMerge(invoice, securityContext);

        return invoice;
    }

    private boolean updateInvoiceNoMerge(Invoice invoice, InvoiceCreate invoiceCreate) {
        boolean update = basicService.updateBasicNoMerge(invoiceCreate, invoice);
        if (invoiceCreate.getReceipt() != null && (invoice.getReceipt() == null || !invoiceCreate.getReceipt().getId().equals(invoice.getReceipt().getId()))) {
            invoice.setReceipt(invoiceCreate.getReceipt());
            update = true;
        }


        if (invoiceCreate.getInvoiceDate() != null && !invoiceCreate.getInvoiceDate().equals(invoice.getInvoiceDate())) {
            invoice.setInvoiceDate(invoiceCreate.getInvoiceDate());
            update = true;
        }

        if (invoiceCreate.getInvoiceReference() != null && !invoiceCreate.getInvoiceReference().equals(invoice.getInvoiceReference())) {
            invoice.setInvoiceReference(invoiceCreate.getInvoiceReference());
            update = true;
        }

        return update;
    }

    public Invoice updateInvoice(InvoiceUpdate invoiceUpdate,
                                 SecurityContext securityContext) {
        Invoice invoice = invoiceUpdate.getInvoice();
        if (updateInvoiceNoMerge(invoice, invoiceUpdate)) {
            repository.merge(invoice);
        }
        return invoice;
    }

    public void validate(InvoiceCreate invoiceCreate,
                         SecurityContext securityContext) {
        basicService.validate(invoiceCreate, securityContext);


    }
}
