package com.wizzdi.flexicore.billing.service;


import com.wizzdi.flexicore.billing.data.PaymentMethodRepository;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethod;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethodType;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethodType_;
import com.wizzdi.flexicore.billing.request.PaymentMethodCreate;
import com.wizzdi.flexicore.billing.request.PaymentMethodFiltering;
import com.wizzdi.flexicore.billing.request.PaymentMethodUpdate;
import com.flexicore.model.Basic;
import com.flexicore.organization.model.Customer_;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.model.Customer;
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

public class PaymentMethodService implements Plugin {

    @Autowired
    private PaymentMethodRepository repository;

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

    public void validateFiltering(PaymentMethodFiltering filtering,
                                  SecurityContext securityContext) {
        basicService.validate(filtering, securityContext);

        Set<String> customerIds = filtering.getCustomerIds();
        Map<String, Customer> customerMap = customerIds.isEmpty() ? new HashMap<>() : listByIds(Customer.class, customerIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        customerIds.removeAll(customerMap.keySet());
        if (!customerIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Customer with ids " + customerIds);
        }
        filtering.setCustomers(new ArrayList<>(customerMap.values()));

        Set<String> paymentMethodTypeIds = filtering.getPaymentMethodTypeIds();
        Map<String, PaymentMethodType> paymentMethodTypeMap = paymentMethodTypeIds.isEmpty() ? new HashMap<>() : listByIds(PaymentMethodType.class, paymentMethodTypeIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        paymentMethodTypeIds.removeAll(paymentMethodTypeMap.keySet());
        if (!paymentMethodTypeIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PaymentMethodType with ids " + paymentMethodTypeIds);
        }
        filtering.setPaymentMethodTypes(new ArrayList<>(paymentMethodTypeMap.values()));
    }

    public PaginationResponse<PaymentMethod> getAllPaymentMethods(
            SecurityContext securityContext, PaymentMethodFiltering filtering) {
        List<PaymentMethod> list = listAllPaymentMethods(securityContext, filtering);
        long count = repository.countAllPaymentMethods(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<PaymentMethod> listAllPaymentMethods(SecurityContext securityContext, PaymentMethodFiltering filtering) {
        return repository.getAllPaymentMethods(securityContext, filtering);
    }

    public PaymentMethod createPaymentMethod(PaymentMethodCreate paymentMethodCreate,
                                             SecurityContext securityContext) {
        PaymentMethod paymentMethod = createPaymentMethodNoMerge(paymentMethodCreate, securityContext);
        repository.merge(paymentMethod);
        return paymentMethod;
    }

    private PaymentMethod createPaymentMethodNoMerge(PaymentMethodCreate paymentMethodCreate,
                                                     SecurityContext securityContext) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(UUID.randomUUID().toString());

        updatePaymentMethodNoMerge(paymentMethod, paymentMethodCreate);
        BaseclassService.createSecurityObjectNoMerge(paymentMethod, securityContext);

        return paymentMethod;
    }

    private boolean updatePaymentMethodNoMerge(PaymentMethod paymentMethod,
                                               PaymentMethodCreate paymentMethodCreate) {
        boolean update = basicService.updateBasicNoMerge(paymentMethodCreate, paymentMethod);
        if (paymentMethodCreate.getCustomer() != null && (paymentMethod.getCustomer() == null || !paymentMethodCreate.getCustomer().getId().equals(paymentMethod.getCustomer().getId()))) {
            paymentMethod.setCustomer(paymentMethodCreate.getCustomer());
            update = true;
        }
        if (paymentMethodCreate.getPaymentMethodType() != null && (paymentMethod.getPaymentMethodType() == null || !paymentMethodCreate.getPaymentMethodType().getId().equals(paymentMethod.getPaymentMethodType().getId()))) {
            paymentMethod.setPaymentMethodType(paymentMethodCreate.getPaymentMethodType());
            update = true;
        }

        if (paymentMethodCreate.getActive() != null && !paymentMethodCreate.getActive().equals(paymentMethod.isActive())) {
            paymentMethod.setActive(paymentMethodCreate.getActive());
            update = true;
        }
        return update;
    }

    public PaymentMethod updatePaymentMethod(PaymentMethodUpdate paymentMethodUpdate,
                                             SecurityContext securityContext) {
        PaymentMethod paymentMethod = paymentMethodUpdate.getPaymentMethod();
        if (updatePaymentMethodNoMerge(paymentMethod, paymentMethodUpdate)) {
            repository.merge(paymentMethod);
        }
        return paymentMethod;
    }

    public void validate(PaymentMethodCreate paymentMethodCreate,
                         SecurityContext securityContext) {
        basicService.validate(paymentMethodCreate, securityContext);
        String customerId = paymentMethodCreate.getCustomerId();
        Customer customer = customerId == null ? null : getByIdOrNull(customerId, Customer.class, null, securityContext);
        if (customer == null && customerId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Customer with id " + customerId);
        }
        paymentMethodCreate.setCustomer(customer);

        String paymentMethodTypeId = paymentMethodCreate.getPaymentMethodTypeId();
        PaymentMethodType paymentMethodType = paymentMethodTypeId == null ? null : getByIdOrNull(paymentMethodTypeId, PaymentMethodType.class, null, securityContext);
        if (paymentMethodType == null && paymentMethodTypeId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No PaymentMethodType with id " + paymentMethodTypeId);
        }
        paymentMethodCreate.setPaymentMethodType(paymentMethodType);


    }
}
