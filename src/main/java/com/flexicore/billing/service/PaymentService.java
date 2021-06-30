package com.flexicore.billing.service;


import com.flexicore.billing.data.PaymentRepository;
import com.flexicore.billing.model.*;
import com.flexicore.billing.model.Currency;
import com.flexicore.billing.request.PaymentCreate;
import com.flexicore.billing.request.PaymentFiltering;
import com.flexicore.billing.request.PaymentUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class PaymentService implements Plugin {

    @Autowired
    private PaymentRepository repository;

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

    public void validateFiltering(PaymentFiltering filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);
        Set<String> invoiceItemIds = filtering.getInvoiceItemIds();
        Map<String, InvoiceItem> invoiceMap = invoiceItemIds.isEmpty() ? new HashMap<>() : listByIds(InvoiceItem.class, invoiceItemIds, Invoice_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        invoiceItemIds.removeAll(invoiceMap.keySet());
        if (!invoiceItemIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No InvoiceItem with ids " + invoiceItemIds);
        }
        filtering.setInvoiceItems(new ArrayList<>(invoiceMap.values()));

        Set<String> contractItemIds = filtering.getContractItemIds();
        Map<String, ContractItem> contractItemMap = contractItemIds.isEmpty() ? new HashMap<>() : listByIds(ContractItem.class, contractItemIds, ContractItem_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        contractItemIds.removeAll(contractItemMap.keySet());
        if (!contractItemIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No ContractItem with ids " + contractItemIds);
        }
        filtering.setContractItems(new ArrayList<>(contractItemMap.values()));

    }

    public PaginationResponse<Payment> getAllPayments(
            SecurityContextBase securityContext, PaymentFiltering filtering) {
        List<Payment> list = listAllPayments(securityContext, filtering);
        long count = repository.countAllPayments(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Payment> listAllPayments(SecurityContextBase securityContext, PaymentFiltering filtering) {
        return repository.getAllPayments(securityContext, filtering);
    }

    public Payment createPayment(PaymentCreate creationContainer,
                                         SecurityContextBase securityContext) {
        Payment payment = createPaymentNoMerge(creationContainer, securityContext);
        repository.merge(payment);
        return payment;
    }

    public Payment createPaymentNoMerge(PaymentCreate creationContainer,
                                                SecurityContextBase securityContext) {
        Payment payment = new Payment();
        payment.setId(Baseclass.getBase64ID());

        updatePaymentNoMerge(payment, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(payment, securityContext);

        return payment;
    }

    private boolean updatePaymentNoMerge(Payment payment,
                                             PaymentCreate creationContainer) {
        boolean update = basicService.updateBasicNoMerge(creationContainer, payment);
        if (creationContainer.getContractItem() != null && (payment.getContractItem() == null || !creationContainer.getContractItem().getId().equals(payment.getContractItem().getId()))) {
            payment.setContractItem(creationContainer.getContractItem());
            update = true;
        }
        if (creationContainer.getContractItem() != null && (payment.getContractItem() == null || !creationContainer.getContractItem().getId().equals(payment.getContractItem().getId()))) {
            payment.setContractItem(creationContainer.getContractItem());
            update = true;
        }
        if (creationContainer.getCurrency() != null && (payment.getCurrency() == null || !creationContainer.getCurrency().getId().equals(payment.getCurrency().getId()))) {
            payment.setCurrency(creationContainer.getCurrency());
            update = true;
        }
        if (creationContainer.getPaymentReference() != null && !creationContainer.getPaymentReference().equals(payment.getPaymentReference())) {
            payment.setPaymentReference(creationContainer.getPaymentReference());
            update = true;
        }
        if (creationContainer.getDatePaid() != null && !creationContainer.getDatePaid().equals(payment.getDatePaid())) {
            payment.setDatePaid(creationContainer.getDatePaid());
            update = true;
        }
        if (creationContainer.getPrice() != null && !creationContainer.getPrice().equals(payment.getPrice())) {
            payment.setPrice(creationContainer.getPrice());
            update = true;
        }


        return update;
    }

    public Payment updatePayment(PaymentUpdate updateContainer,
                                         SecurityContextBase securityContext) {
        Payment payment = updateContainer.getPayment();
        if (updatePaymentNoMerge(payment, updateContainer)) {
            repository.merge(payment);
        }
        return payment;
    }

    public void validate(PaymentCreate creationContainer, SecurityContextBase securityContext) {
        basicService.validate(creationContainer, securityContext);

        String contractItemId = creationContainer.getContractItemId();
        ContractItem contractItem = contractItemId == null ? null : getByIdOrNull(contractItemId, ContractItem.class, null, securityContext);
        if (contractItem == null && contractItemId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No ContractItem with id " + contractItemId);
        }
        creationContainer.setContractItem(contractItem);

        String currencyId = creationContainer.getCurrencyId();
        Currency currency = currencyId == null ? null : getByIdOrNull(currencyId, Currency.class, null, securityContext);
        if (currency == null && currencyId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Currency with id " + currencyId);
        }
        creationContainer.setCurrency(currency);


    }
}