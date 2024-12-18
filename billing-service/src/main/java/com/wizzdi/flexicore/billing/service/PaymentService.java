package com.wizzdi.flexicore.billing.service;


import com.wizzdi.flexicore.billing.data.PaymentRepository;
import com.wizzdi.flexicore.billing.request.PaymentCreate;
import com.wizzdi.flexicore.billing.request.PaymentFiltering;
import com.wizzdi.flexicore.billing.request.PaymentUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.billing.model.payment.Payment;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.price.Money;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;

@Extension
@Component

public class PaymentService implements Plugin {

    @Autowired
    private PaymentRepository repository;

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

    public void validateFiltering(PaymentFiltering filtering,
                                  SecurityContext securityContext) {
        basicService.validate(filtering, securityContext);


    }

    public PaginationResponse<Payment> getAllPayments(
            SecurityContext securityContext, PaymentFiltering filtering) {
        List<Payment> list = listAllPayments(securityContext, filtering);
        long count = repository.countAllPayments(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Payment> listAllPayments(SecurityContext securityContext, PaymentFiltering filtering) {
        return repository.getAllPayments(securityContext, filtering);
    }

    public Payment createPayment(PaymentCreate paymentCreate,
                                         SecurityContext securityContext) {
        Payment payment = createPaymentNoMerge(paymentCreate, securityContext);
        repository.merge(payment);
        return payment;
    }

    public Payment createPaymentNoMerge(PaymentCreate paymentCreate,
                                                SecurityContext securityContext) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());

        updatePaymentNoMerge(payment, paymentCreate);
        BaseclassService.createSecurityObjectNoMerge(payment, securityContext);

        return payment;
    }

    private boolean updatePaymentNoMerge(Payment payment,
                                             PaymentCreate paymentCreate) {
        boolean update = basicService.updateBasicNoMerge(paymentCreate, payment);
        if (paymentCreate.getMoney() != null && (payment.getMoney() == null || !paymentCreate.getMoney().getId().equals(payment.getMoney().getId()))) {
            payment.setMoney(paymentCreate.getMoney());
            update = true;
        }
        if (paymentCreate.getPaymentReference() != null && !paymentCreate.getPaymentReference().equals(payment.getPaymentReference())) {
            payment.setPaymentReference(paymentCreate.getPaymentReference());
            update = true;
        }
        if (paymentCreate.getDatePaid() != null && !paymentCreate.getDatePaid().equals(payment.getDatePaid())) {
            payment.setDatePaid(paymentCreate.getDatePaid());
            update = true;
        }


        return update;
    }

    public Payment updatePayment(PaymentUpdate paymentUpdate,
                                         SecurityContext securityContext) {
        Payment payment = paymentUpdate.getPayment();
        if (updatePaymentNoMerge(payment, paymentUpdate)) {
            repository.merge(payment);
        }
        return payment;
    }

    public void validate(PaymentCreate paymentCreate, SecurityContext securityContext) {
        basicService.validate(paymentCreate, securityContext);

        String moneyId = paymentCreate.getMoneyId();
        Money money = moneyId == null ? null : getByIdOrNull(moneyId, Money.class, null, securityContext);
        if (money == null && moneyId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Money with id " + moneyId);
        }
        paymentCreate.setMoney(money);


    }
}
