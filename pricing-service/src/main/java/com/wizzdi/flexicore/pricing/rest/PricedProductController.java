package com.wizzdi.flexicore.pricing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct_;
import com.wizzdi.flexicore.pricing.request.PricedProductCreate;
import com.wizzdi.flexicore.pricing.request.PricedProductFiltering;
import com.wizzdi.flexicore.pricing.request.PricedProductUpdate;
import com.wizzdi.flexicore.pricing.service.PricedProductService;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/PricedProduct")

@Tag(name = "PricedProduct")
@Extension
@RestController
public class PricedProductController implements Plugin {

    @Autowired
    private PricedProductService service;


    @Operation(summary = "getAllPricedProducts", description = "Lists all PricedProducts")
    @IOperation(Name = "getAllPricedProducts", Description = "Lists all PricedProducts")
    @PostMapping("/getAllPricedProducts")
    public PaginationResponse<PricedProduct> getAllPricedProducts(

            
            @RequestBody PricedProductFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllPricedProducts(securityContext, filtering);
    }


    @PostMapping("/createPricedProduct")
    @Operation(summary = "createPricedProduct", description = "Creates PricedProduct")
    @IOperation(Name = "createPricedProduct", Description = "Creates PricedProduct")
    public PricedProduct createPricedProduct(

            
            @RequestBody PricedProductCreate creationContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createPricedProduct(creationContainer, securityContext);
    }


    @PutMapping("/updatePricedProduct")
    @Operation(summary = "updatePricedProduct", description = "Updates PricedProduct")
    @IOperation(Name = "updatePricedProduct", Description = "Updates PricedProduct")
    public PricedProduct updatePricedProduct(

            
            @RequestBody PricedProductUpdate updateContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(updateContainer, securityContext);
        PricedProduct pricedProduct = service.getByIdOrNull(updateContainer.getId(),
                PricedProduct.class, PricedProduct_.security, securityContext);
        if (pricedProduct == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no PricedProduct with id "
                    + updateContainer.getId());
        }
        updateContainer.setPricedProduct(pricedProduct);

        return service.updatePricedProduct(updateContainer, securityContext);
    }
}