package com.wizzdi.flexicore.billing.service;


import com.wizzdi.flexicore.billing.data.ChargeReferenceRepository;
import com.wizzdi.flexicore.billing.request.ChargeReferenceCreate;
import com.wizzdi.flexicore.billing.request.ChargeReferenceFiltering;
import com.wizzdi.flexicore.billing.request.ChargeReferenceUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.billing.model.billing.ChargeReference;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
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

public class ChargeReferenceService implements Plugin {

    @Autowired
    private ChargeReferenceRepository repository;

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

    public void validateFiltering(ChargeReferenceFiltering filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);


    }

    public PaginationResponse<ChargeReference> getAllChargeReferences(
            SecurityContextBase securityContext, ChargeReferenceFiltering filtering) {
        List<ChargeReference> list = listAllChargeReferences(securityContext, filtering);
        long count = repository.countAllChargeReferences(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<ChargeReference> listAllChargeReferences(SecurityContextBase securityContext, ChargeReferenceFiltering filtering) {
        return repository.getAllChargeReferences(securityContext, filtering);
    }

    public ChargeReference createChargeReference(ChargeReferenceCreate chargeReferenceCreate,
                               SecurityContextBase securityContext) {
        ChargeReference chargeReference = createChargeReferenceNoMerge(chargeReferenceCreate, securityContext);
        repository.merge(chargeReference);
        return chargeReference;
    }

    public ChargeReference createChargeReferenceNoMerge(ChargeReferenceCreate chargeReferenceCreate,
                                      SecurityContextBase securityContext) {
        ChargeReference chargeReference = new ChargeReference();
        chargeReference.setId(Baseclass.getBase64ID());

        updateChargeReferenceNoMerge(chargeReference, chargeReferenceCreate);
        BaseclassService.createSecurityObjectNoMerge(chargeReference, securityContext);

        return chargeReference;
    }

    public boolean updateChargeReferenceNoMerge(ChargeReference chargeReference,
                                        ChargeReferenceCreate chargeReferenceCreate) {
        boolean update = basicService.updateBasicNoMerge(chargeReferenceCreate, chargeReference);

        if (chargeReferenceCreate.getChargeReference() != null &&  !chargeReferenceCreate.getChargeReference().equals(chargeReference.getChargeReference())) {
            chargeReference.setChargeReference(chargeReferenceCreate.getChargeReference());
            update = true;
        }

        return update;
    }

    public ChargeReference updateChargeReference(ChargeReferenceUpdate chargeReferenceUpdate,
                                                 SecurityContextBase securityContext) {
        ChargeReference chargeReference = chargeReferenceUpdate.getChargeReferenceObject();
        if (updateChargeReferenceNoMerge(chargeReference, chargeReferenceUpdate)) {
            repository.merge(chargeReference);
        }
        return chargeReference;
    }

    public void validate(ChargeReferenceCreate chargeReferenceCreate, SecurityContextBase securityContext) {
        basicService.validate(chargeReferenceCreate, securityContext);



    }
}