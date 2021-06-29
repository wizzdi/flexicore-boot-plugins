package com.flexicore.billing.service;


import com.flexicore.billing.data.PaymentMethodRepository;
import com.flexicore.billing.model.ContractItem;
import com.flexicore.billing.model.PaymentMethod;
import com.flexicore.billing.model.PaymentMethodType;
import com.flexicore.billing.model.PaymentMethodType_;
import com.flexicore.billing.request.PaymentMethodCreate;
import com.flexicore.billing.request.PaymentMethodFiltering;
import com.flexicore.billing.request.PaymentMethodUpdate;
import com.flexicore.model.Basic;
import com.flexicore.organization.model.Customer_;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.model.Customer;
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

public class PaymentMethodService implements Plugin {

    @Autowired
    private PaymentMethodRepository repository;

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

    public void validateFiltering(PaymentMethodFiltering filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);

        Set<String> customerIds = filtering.getCustomerIds();
        Map<String, Customer> customerMap = customerIds.isEmpty() ? new HashMap<>() : listByIds(Customer.class, customerIds, Customer_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        customerIds.removeAll(customerMap.keySet());
        if (!customerIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Customer with ids " + customerIds);
        }
        filtering.setCustomers(new ArrayList<>(customerMap.values()));

        Set<String> paymentMethodTypeIds = filtering.getPaymentMethodTypeIds();
        Map<String, PaymentMethodType> paymentMethodTypeMap = paymentMethodTypeIds.isEmpty() ? new HashMap<>() : listByIds(PaymentMethodType.class, paymentMethodTypeIds, PaymentMethodType_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        paymentMethodTypeIds.removeAll(paymentMethodTypeMap.keySet());
        if (!paymentMethodTypeIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PaymentMethodType with ids " + paymentMethodTypeIds);
        }
        filtering.setPaymentMethodTypes(new ArrayList<>(paymentMethodTypeMap.values()));
    }

    public PaginationResponse<PaymentMethod> getAllPaymentMethods(
            SecurityContextBase securityContext, PaymentMethodFiltering filtering) {
        List<PaymentMethod> list = listAllPaymentMethods(securityContext, filtering);
        long count = repository.countAllPaymentMethods(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<PaymentMethod> listAllPaymentMethods(SecurityContextBase securityContext, PaymentMethodFiltering filtering) {
        return repository.getAllPaymentMethods(securityContext, filtering);
    }

    public PaymentMethod createPaymentMethod(PaymentMethodCreate creationContainer,
                                             SecurityContextBase securityContext) {
        PaymentMethod paymentMethod = createPaymentMethodNoMerge(creationContainer, securityContext);
        repository.merge(paymentMethod);
        return paymentMethod;
    }

    private PaymentMethod createPaymentMethodNoMerge(PaymentMethodCreate creationContainer,
                                                     SecurityContextBase securityContext) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(Baseclass.getBase64ID());

        updatePaymentMethodNoMerge(paymentMethod, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(paymentMethod, securityContext);

        return paymentMethod;
    }

    private boolean updatePaymentMethodNoMerge(PaymentMethod paymentMethod,
                                               PaymentMethodCreate creationContainer) {
        boolean update = basicService.updateBasicNoMerge(creationContainer, paymentMethod);
        if (creationContainer.getCustomer() != null && (paymentMethod.getCustomer() == null || !creationContainer.getCustomer().getId().equals(paymentMethod.getCustomer().getId()))) {
            paymentMethod.setCustomer(creationContainer.getCustomer());
            update = true;
        }
        if (creationContainer.getPaymentMethodType() != null && (paymentMethod.getPaymentMethodType() == null || !creationContainer.getPaymentMethodType().getId().equals(paymentMethod.getPaymentMethodType().getId()))) {
            paymentMethod.setPaymentMethodType(creationContainer.getPaymentMethodType());
            update = true;
        }

        if (creationContainer.getActive() != null && !creationContainer.getActive().equals(paymentMethod.isActive())) {
            paymentMethod.setActive(creationContainer.getActive());
            update = true;
        }
        return update;
    }

    public PaymentMethod updatePaymentMethod(PaymentMethodUpdate updateContainer,
                                             SecurityContextBase securityContext) {
        PaymentMethod paymentMethod = updateContainer.getPaymentMethod();
        if (updatePaymentMethodNoMerge(paymentMethod, updateContainer)) {
            repository.merge(paymentMethod);
        }
        return paymentMethod;
    }

    public void validate(PaymentMethodCreate creationContainer,
                         SecurityContextBase securityContext) {
        basicService.validate(creationContainer, securityContext);
        String customerId = creationContainer.getCustomerId();
        Customer customer = customerId == null ? null : getByIdOrNull(customerId, Customer.class, null, securityContext);
        if (customer == null && customerId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Customer with id " + customerId);
        }
        creationContainer.setCustomer(customer);

        String paymentMethodTypeId = creationContainer.getPaymentMethodTypeId();
        PaymentMethodType paymentMethodType = paymentMethodTypeId == null ? null : getByIdOrNull(paymentMethodTypeId, PaymentMethodType.class, null, securityContext);
        if (paymentMethodType == null && paymentMethodTypeId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PaymentMethodType with id " + paymentMethodTypeId);
        }
        creationContainer.setPaymentMethodType(paymentMethodType);


    }
}