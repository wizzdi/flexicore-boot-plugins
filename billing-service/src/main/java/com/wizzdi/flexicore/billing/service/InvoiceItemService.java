package com.wizzdi.flexicore.billing.service;


import com.wizzdi.flexicore.billing.data.InvoiceItemRepository;

import com.wizzdi.flexicore.billing.model.billing.Charge;
import com.wizzdi.flexicore.billing.model.billing.Charge_;
import com.wizzdi.flexicore.billing.model.payment.*;
import com.wizzdi.flexicore.billing.request.InvoiceItemCreate;
import com.wizzdi.flexicore.billing.request.InvoiceItemFiltering;
import com.wizzdi.flexicore.billing.request.InvoiceItemUpdate;
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

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class InvoiceItemService implements Plugin {

    @Autowired
    private InvoiceItemRepository repository;

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

    public void validateFiltering(InvoiceItemFiltering filtering,
                                  SecurityContext securityContext) {
        basicService.validate(filtering, securityContext);
        Set<String> invoiceIds = filtering.getInvoiceIds();
        Map<String, Invoice> invoiceMap = invoiceIds.isEmpty() ? new HashMap<>() : listByIds(Invoice.class, invoiceIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        invoiceIds.removeAll(invoiceMap.keySet());
        if (!invoiceIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Invoice with ids " + invoiceIds);
        }
        filtering.setInvoices(new ArrayList<>(invoiceMap.values()));

        Set<String> chargeIds = filtering.getChargeIds();
        Map<String, Charge> chargeMap = chargeIds.isEmpty() ? new HashMap<>() : listByIds(Charge.class, chargeIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        chargeIds.removeAll(chargeMap.keySet());
        if (!chargeIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Charge with ids " + chargeIds);
        }
        filtering.setCharges(new ArrayList<>(chargeMap.values()));

    }

    public PaginationResponse<InvoiceItem> getAllInvoiceItems(
            SecurityContext securityContext, InvoiceItemFiltering filtering) {
        List<InvoiceItem> list = listAllInvoiceItems(securityContext, filtering);
        long count = repository.countAllInvoiceItems(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<InvoiceItem> listAllInvoiceItems(SecurityContext securityContext, InvoiceItemFiltering filtering) {
        return repository.getAllInvoiceItems(securityContext, filtering);
    }

    public InvoiceItem createInvoiceItem(InvoiceItemCreate invoiceItemCreate,
                                         SecurityContext securityContext) {
        InvoiceItem invoiceItem = createInvoiceItemNoMerge(invoiceItemCreate, securityContext);
        repository.merge(invoiceItem);
        return invoiceItem;
    }

    public InvoiceItem createInvoiceItemNoMerge(InvoiceItemCreate invoiceItemCreate,
                                                SecurityContext securityContext) {
        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setId(UUID.randomUUID().toString());

        updateInvoiceItemNoMerge(invoiceItem, invoiceItemCreate);
        BaseclassService.createSecurityObjectNoMerge(invoiceItem, securityContext);

        return invoiceItem;
    }

    private boolean updateInvoiceItemNoMerge(InvoiceItem invoiceItem,
                                             InvoiceItemCreate invoiceItemCreate) {
        boolean update = basicService.updateBasicNoMerge(invoiceItemCreate, invoiceItem);
        if (invoiceItemCreate.getInvoice() != null && (invoiceItem.getInvoice() == null || !invoiceItemCreate.getInvoice().getId().equals(invoiceItem.getInvoice().getId()))) {
            invoiceItem.setInvoice(invoiceItemCreate.getInvoice());
            update = true;
        }
        if (invoiceItemCreate.getCharge() != null && (invoiceItem.getCharge() == null || !invoiceItemCreate.getCharge().getId().equals(invoiceItem.getCharge().getId()))) {
            invoiceItem.setCharge(invoiceItemCreate.getCharge());
            update = true;
        }
        return update;
    }

    public InvoiceItem updateInvoiceItem(InvoiceItemUpdate invoiceItemUpdate,
                                         SecurityContext securityContext) {
        InvoiceItem invoiceItem = invoiceItemUpdate.getInvoiceItem();
        if (updateInvoiceItemNoMerge(invoiceItem, invoiceItemUpdate)) {
            repository.merge(invoiceItem);
        }
        return invoiceItem;
    }

    public void validate(InvoiceItemCreate invoiceItemCreate, SecurityContext securityContext) {
        basicService.validate(invoiceItemCreate, securityContext);

        String invoiceId = invoiceItemCreate.getInvoiceId();
        Invoice invoice = invoiceId == null ? null : getByIdOrNull(invoiceId, Invoice.class,securityContext);

        if (invoice == null && invoiceId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Invoice with id " + invoiceId);
        }
        invoiceItemCreate.setInvoice(invoice);

        String chargeId = invoiceItemCreate.getChargeId();
        Charge charge = chargeId == null ? null : getByIdOrNull(chargeId, Charge.class,securityContext);
        if (charge == null && chargeId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Charge with id " + chargeId);
        }
        invoiceItemCreate.setCharge(charge);


    }
}
