package com.flexicore.billing.service;


import com.flexicore.billing.data.PaymentMethodTypeRepository;
import com.flexicore.billing.model.PaymentMethodType;
import com.flexicore.billing.request.PaymentMethodTypeCreate;
import com.flexicore.billing.request.PaymentMethodTypeFiltering;
import com.flexicore.billing.request.PaymentMethodTypeUpdate;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;

@Extension
@Component

public class PaymentMethodTypeService implements Plugin {

        @Autowired
    private PaymentMethodTypeRepository repository;

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

    public void validateFiltering(PaymentMethodTypeFiltering filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);
    }

    public PaginationResponse<PaymentMethodType> getAllPaymentMethodTypes(
            SecurityContextBase securityContext, PaymentMethodTypeFiltering filtering) {
        List<PaymentMethodType> list = listAllPaymentMethodTypes(securityContext, filtering);
        long count = repository.countAllPaymentMethodTypes(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<PaymentMethodType> listAllPaymentMethodTypes(SecurityContextBase securityContext, PaymentMethodTypeFiltering filtering) {
        return repository.getAllPaymentMethodTypes(securityContext, filtering);
    }

    public PaymentMethodType createPaymentMethodType(PaymentMethodTypeCreate creationContainer,
                                                     SecurityContextBase securityContext) {
        PaymentMethodType paymentMethodType = createPaymentMethodTypeNoMerge(creationContainer, securityContext);
        repository.merge(paymentMethodType);
        return paymentMethodType;
    }

    private PaymentMethodType createPaymentMethodTypeNoMerge(PaymentMethodTypeCreate creationContainer,
                                                             SecurityContextBase securityContext) {
        PaymentMethodType paymentMethodType = new PaymentMethodType();
        paymentMethodType.setId(Baseclass.getBase64ID());

        updatePaymentMethodTypeNoMerge(paymentMethodType, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(paymentMethodType,securityContext);

        return paymentMethodType;
    }

    private boolean updatePaymentMethodTypeNoMerge(PaymentMethodType paymentMethodType,
                                                   PaymentMethodTypeCreate creationContainer) {
        boolean update = basicService.updateBasicNoMerge(creationContainer, paymentMethodType);
        if (creationContainer.getCanonicalClassName() != null && !creationContainer.getCanonicalClassName().equals(paymentMethodType.getCanonicalClassName())) {
            paymentMethodType.setCanonicalClassName(creationContainer.getCanonicalClassName());
            update = true;
        }

        return update;
    }

    public PaymentMethodType updatePaymentMethodType(PaymentMethodTypeUpdate updateContainer,
                                                     SecurityContextBase securityContext) {
        PaymentMethodType paymentMethodType = updateContainer.getPaymentMethodType();
        if (updatePaymentMethodTypeNoMerge(paymentMethodType, updateContainer)) {
            repository.merge(paymentMethodType);
        }
        return paymentMethodType;
    }

    public void validate(PaymentMethodTypeCreate creationContainer,
                         SecurityContextBase securityContext) {
        basicService.validate(creationContainer, securityContext);
    }
}