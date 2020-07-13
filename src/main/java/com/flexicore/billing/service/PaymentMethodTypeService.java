package com.flexicore.billing.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.data.PaymentMethodTypeRepository;
import com.flexicore.billing.model.PaymentMethodType;
import com.flexicore.billing.request.PaymentMethodTypeCreate;
import com.flexicore.billing.request.PaymentMethodTypeFiltering;
import com.flexicore.billing.request.PaymentMethodTypeUpdate;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@PluginInfo(version = 1)
@Extension
@Component
@Primary
public class PaymentMethodTypeService implements ServicePlugin {

    @PluginInfo(version = 1)
    @Autowired
    private PaymentMethodTypeRepository repository;

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

    public void validateFiltering(PaymentMethodTypeFiltering filtering,
                                  SecurityContext securityContext) {
        baseclassNewService.validateFilter(filtering, securityContext);
    }

    public PaginationResponse<PaymentMethodType> getAllPaymentMethodTypes(
            SecurityContext securityContext, PaymentMethodTypeFiltering filtering) {
        List<PaymentMethodType> list = repository.getAllPaymentMethodTypes(securityContext, filtering);
        long count = repository.countAllPaymentMethodTypes(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public PaymentMethodType createPaymentMethodType(PaymentMethodTypeCreate creationContainer,
                                                     SecurityContext securityContext) {
        PaymentMethodType paymentMethodType = createPaymentMethodTypeNoMerge(creationContainer, securityContext);
        repository.merge(paymentMethodType);
        return paymentMethodType;
    }

    private PaymentMethodType createPaymentMethodTypeNoMerge(PaymentMethodTypeCreate creationContainer,
                                                             SecurityContext securityContext) {
        PaymentMethodType paymentMethodType = new PaymentMethodType(creationContainer.getName(), securityContext);
        updatePaymentMethodTypeNoMerge(paymentMethodType, creationContainer);
        return paymentMethodType;
    }

    private boolean updatePaymentMethodTypeNoMerge(PaymentMethodType paymentMethodType,
                                                   PaymentMethodTypeCreate creationContainer) {
        boolean update = baseclassNewService.updateBaseclassNoMerge(creationContainer, paymentMethodType);
        if (creationContainer.getCanonicalClassName() != null && !creationContainer.getCanonicalClassName().equals(paymentMethodType.getCanonicalClassName())) {
            paymentMethodType.setCanonicalClassName(creationContainer.getCanonicalClassName());
            update = true;
        }

        return update;
    }

    public PaymentMethodType updatePaymentMethodType(PaymentMethodTypeUpdate updateContainer,
                                                     SecurityContext securityContext) {
        PaymentMethodType paymentMethodType = updateContainer.getPaymentMethodType();
        if (updatePaymentMethodTypeNoMerge(paymentMethodType, updateContainer)) {
            repository.merge(paymentMethodType);
        }
        return paymentMethodType;
    }

    public void validate(PaymentMethodTypeCreate creationContainer,
                         SecurityContext securityContext) {
        baseclassNewService.validate(creationContainer, securityContext);
    }
}