package com.wizzdi.flexicore.billing.service;


import com.wizzdi.flexicore.billing.data.PaymentMethodTypeRepository;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethodType;
import com.wizzdi.flexicore.billing.request.PaymentMethodTypeCreate;
import com.wizzdi.flexicore.billing.request.PaymentMethodTypeFiltering;
import com.wizzdi.flexicore.billing.request.PaymentMethodTypeUpdate;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Extension
@Component

public class PaymentMethodTypeService implements Plugin {

        @Autowired
    private PaymentMethodTypeRepository repository;

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

    public void validateFiltering(PaymentMethodTypeFiltering filtering,
                                  SecurityContext securityContext) {
        basicService.validate(filtering, securityContext);
    }

    public PaginationResponse<PaymentMethodType> getAllPaymentMethodTypes(
            SecurityContext securityContext, PaymentMethodTypeFiltering filtering) {
        List<PaymentMethodType> list = listAllPaymentMethodTypes(securityContext, filtering);
        long count = repository.countAllPaymentMethodTypes(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<PaymentMethodType> listAllPaymentMethodTypes(SecurityContext securityContext, PaymentMethodTypeFiltering filtering) {
        return repository.getAllPaymentMethodTypes(securityContext, filtering);
    }

    public PaymentMethodType createPaymentMethodType(PaymentMethodTypeCreate paymentMethodTypeCreate,
                                                     SecurityContext securityContext) {
        PaymentMethodType paymentMethodType = createPaymentMethodTypeNoMerge(paymentMethodTypeCreate, securityContext);
        repository.merge(paymentMethodType);
        return paymentMethodType;
    }

    private PaymentMethodType createPaymentMethodTypeNoMerge(PaymentMethodTypeCreate paymentMethodTypeCreate,
                                                             SecurityContext securityContext) {
        PaymentMethodType paymentMethodType = new PaymentMethodType();
        paymentMethodType.setId(UUID.randomUUID().toString());

        updatePaymentMethodTypeNoMerge(paymentMethodType, paymentMethodTypeCreate);
        BaseclassService.createSecurityObjectNoMerge(paymentMethodType,securityContext);

        return paymentMethodType;
    }

    private boolean updatePaymentMethodTypeNoMerge(PaymentMethodType paymentMethodType,
                                                   PaymentMethodTypeCreate paymentMethodTypeCreate) {
        boolean update = basicService.updateBasicNoMerge(paymentMethodTypeCreate, paymentMethodType);
        if (paymentMethodTypeCreate.getCanonicalClassName() != null && !paymentMethodTypeCreate.getCanonicalClassName().equals(paymentMethodType.getCanonicalClassName())) {
            paymentMethodType.setCanonicalClassName(paymentMethodTypeCreate.getCanonicalClassName());
            update = true;
        }

        return update;
    }

    public PaymentMethodType updatePaymentMethodType(PaymentMethodTypeUpdate paymentMethodTypeUpdate,
                                                     SecurityContext securityContext) {
        PaymentMethodType paymentMethodType = paymentMethodTypeUpdate.getPaymentMethodType();
        if (updatePaymentMethodTypeNoMerge(paymentMethodType, paymentMethodTypeUpdate)) {
            repository.merge(paymentMethodType);
        }
        return paymentMethodType;
    }

    public void validate(PaymentMethodTypeCreate paymentMethodTypeCreate,
                         SecurityContext securityContext) {
        basicService.validate(paymentMethodTypeCreate, securityContext);
    }
}
