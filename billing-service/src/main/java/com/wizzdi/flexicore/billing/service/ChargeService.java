package com.wizzdi.flexicore.billing.service;


import com.wizzdi.flexicore.billing.data.ChargeRepository;
import com.wizzdi.flexicore.billing.request.ChargeCreate;
import com.wizzdi.flexicore.billing.request.ChargeFiltering;
import com.wizzdi.flexicore.billing.request.ChargeUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.billing.model.billing.Charge;
import com.wizzdi.flexicore.billing.model.billing.ChargeReference;
import com.wizzdi.flexicore.billing.model.payment.InvoiceItem;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.price.Money;
import com.wizzdi.flexicore.pricing.model.price.Money_;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Extension
@Component

public class ChargeService implements Plugin {

    @Autowired
    private ChargeRepository repository;

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

    public void validateFiltering(ChargeFiltering filtering,
                                  SecurityContext securityContext) {
        basicService.validate(filtering, securityContext);


    }

    public PaginationResponse<Charge> getAllCharges(
            SecurityContext securityContext, ChargeFiltering filtering) {
        List<Charge> list = listAllCharges(securityContext, filtering);
        long count = repository.countAllCharges(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Charge> listAllCharges(SecurityContext securityContext, ChargeFiltering filtering) {
        return repository.getAllCharges(securityContext, filtering);
    }

    public Charge createCharge(ChargeCreate chargeCreate,
                               SecurityContext securityContext) {
        Charge charge = createChargeNoMerge(chargeCreate, securityContext);
        repository.merge(charge);
        return charge;
    }

    public Charge createChargeNoMerge(ChargeCreate chargeCreate,
                                      SecurityContext securityContext) {
        Charge charge = new Charge();
        charge.setId(UUID.randomUUID().toString());

        updateChargeNoMerge(charge, chargeCreate);
        BaseclassService.createSecurityObjectNoMerge(charge, securityContext);

        return charge;
    }

    private boolean updateChargeNoMerge(Charge charge,
                                        ChargeCreate chargeCreate) {
        boolean update = basicService.updateBasicNoMerge(chargeCreate, charge);
        if (chargeCreate.getMoney() != null && (charge.getMoney() == null || !chargeCreate.getMoney().getId().equals(charge.getMoney().getId()))) {
            charge.setMoney(chargeCreate.getMoney());
            update = true;
        }
        if (chargeCreate.getChargeReference() != null && (charge.getChargeReference() == null || !chargeCreate.getChargeReference().getId().equals(charge.getChargeReference().getId()))) {
            charge.setChargeReference(chargeCreate.getChargeReference());
            update = true;
        }
        if (chargeCreate.getInvoiceItem() != null && (charge.getInvoiceItem() == null || !chargeCreate.getInvoiceItem().getId().equals(charge.getInvoiceItem().getId()))) {
            charge.setInvoiceItem(chargeCreate.getInvoiceItem());
            update = true;
        }
        if (chargeCreate.getChargeDate() != null && !chargeCreate.getChargeDate().equals(charge.getChargeDate())) {
            charge.setChargeDate(chargeCreate.getChargeDate());
            update = true;
        }


        return update;
    }

    public Charge updateCharge(ChargeUpdate chargeUpdate,
                               SecurityContext securityContext) {
        Charge charge = chargeUpdate.getCharge();
        if (updateChargeNoMerge(charge, chargeUpdate)) {
            repository.merge(charge);
        }
        return charge;
    }

    public void validate(ChargeCreate chargeCreate, SecurityContext securityContext) {
        basicService.validate(chargeCreate, securityContext);

        String moneyId = chargeCreate.getMoneyId();
        Money money = moneyId == null ? null : getByIdOrNull(moneyId, Money.class,securityContext);
        if (money == null && moneyId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Money with id " + moneyId);
        }
        chargeCreate.setMoney(money);

        String chargeReferenceId = chargeCreate.getChargeReferenceId();
        ChargeReference chargeReference = chargeReferenceId == null ? null : getByIdOrNull(chargeReferenceId, ChargeReference.class,securityContext);
        if (chargeReference == null && chargeReferenceId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No ChargeReference with id " + chargeReferenceId);
        }
        chargeCreate.setChargeReference(chargeReference);

        String invoiceItemId = chargeCreate.getInvoiceItemId();
        InvoiceItem invoiceItem = invoiceItemId == null ? null : getByIdOrNull(invoiceItemId, InvoiceItem.class,securityContext);
        if (invoiceItem == null && invoiceItemId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No InvoiceItem with id " + invoiceItemId);
        }
        chargeCreate.setInvoiceItem(invoiceItem);


    }
}
