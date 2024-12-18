package com.flexicore.license.service;


import com.flexicore.license.data.LicenseRequestToProductRepository;
import com.flexicore.license.model.LicenseRequestToProduct;
import com.flexicore.license.model.LicensingProduct;
import com.flexicore.license.model.LicensingProduct_;
import com.flexicore.license.request.LicenseRequestToProductCreate;
import com.flexicore.license.request.LicenseRequestToProductFiltering;
import com.flexicore.license.request.LicenseRequestToProductUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Extension
@Component
public class LicenseRequestToProductService implements Plugin {


    @Autowired

    private LicenseRequestToProductRepository repository;

    @Autowired

    private LicenseRequestToEntityService licenseRequestToEntityService;



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
    public LicenseRequestToProduct createLicenseRequestToProduct(LicenseRequestToProductCreate pluginCreationContainer, SecurityContext securityContext) {
        LicenseRequestToProduct licenseRequestToProduct = createLicenseRequestToProductNoMerge(pluginCreationContainer, securityContext);
        repository.merge(licenseRequestToProduct);
        licenseRequestUpdateEventEvent.publishEvent(new LicenseRequestUpdateEvent().setLicenseRequest(licenseRequestToProduct.getLicenseRequest()).setSecurityContext(securityContext));

        return licenseRequestToProduct;


    }

    public LicenseRequestToProduct createLicenseRequestToProductNoMerge(LicenseRequestToProductCreate licenseRequestToProductCreate, SecurityContext securityContext) {
        LicenseRequestToProduct licenseRequestToProduct = new LicenseRequestToProduct();
        licenseRequestToProduct.setId(UUID.randomUUID().toString());
        updateLicenseRequestToProductNoMerge(licenseRequestToProduct, licenseRequestToProductCreate);
        BaseclassService.createSecurityObjectNoMerge(licenseRequestToProduct,securityContext);
        return licenseRequestToProduct;
    }

    private boolean updateLicenseRequestToProductNoMerge(LicenseRequestToProduct licenseRequestToProduct, LicenseRequestToProductCreate licenseRequestToProductCreate) {
        boolean update = licenseRequestToEntityService.updateLicenseRequestToEntityNoMerge(licenseRequestToProduct, licenseRequestToProductCreate);
        if(licenseRequestToProductCreate.getLicensingProduct()!=null && (licenseRequestToProduct.getLicensingEntity()==null || !licenseRequestToProductCreate.getLicensingProduct().getId().equals(licenseRequestToProduct.getLicensingEntity().getId()))){
            licenseRequestToProduct.setLicensingEntity(licenseRequestToProductCreate.getLicensingProduct());
            update=true;
        }
        return update;
    }


    @Autowired
    private ApplicationEventPublisher licenseRequestUpdateEventEvent;


    public LicenseRequestToProduct updateLicenseRequestToProduct(LicenseRequestToProductUpdate licenseRequestToProductUpdate, SecurityContext securityContext) {
        LicenseRequestToProduct licenseRequestToProduct = licenseRequestToProductUpdate.getLicenseRequestToProduct();
        if (updateLicenseRequestToProductNoMerge(licenseRequestToProduct, licenseRequestToProductUpdate)) {
            repository.merge(licenseRequestToProduct);
            licenseRequestUpdateEventEvent.publishEvent(new LicenseRequestUpdateEvent().setLicenseRequest(licenseRequestToProduct.getLicenseRequest()).setSecurityContext(securityContext));

        }
        return licenseRequestToProduct;
    }

    public List<LicenseRequestToProduct> listAllLicenseRequestToProducts(LicenseRequestToProductFiltering licenseRequestToProductFiltering, SecurityContext securityContext) {
        return repository.listAllLicenseRequestToProducts(licenseRequestToProductFiltering, securityContext);
    }

    public void validate(LicenseRequestToProductCreate licenseRequestToProductCreate, SecurityContext securityContext) {
        licenseRequestToEntityService.validate(licenseRequestToProductCreate, securityContext);
        String licensingProductId=licenseRequestToProductCreate.getLicensingProductId();
        LicensingProduct licensingProduct=licensingProductId!=null?getByIdOrNull(licensingProductId, LicensingProduct.class, securityContext):null;
        if(licensingProduct==null && licensingProductId!=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No License product with id "+licensingProductId);
        }
        licenseRequestToProductCreate.setLicensingProduct(licensingProduct);

    }

    public void validate(LicenseRequestToProductFiltering licenseRequestToProductFiltering, SecurityContext securityContext) {
        licenseRequestToEntityService.validate(licenseRequestToProductFiltering,securityContext);
    }

    public PaginationResponse<LicenseRequestToProduct> getAllLicenseRequestToProducts(LicenseRequestToProductFiltering licenseRequestToProductFiltering, SecurityContext securityContext) {
        List<LicenseRequestToProduct> list = listAllLicenseRequestToProducts(licenseRequestToProductFiltering, securityContext);
        long count = repository.countAllLicenseRequestToProducts(licenseRequestToProductFiltering, securityContext);
        return new PaginationResponse<>(list, licenseRequestToProductFiltering, count);
    }


}
