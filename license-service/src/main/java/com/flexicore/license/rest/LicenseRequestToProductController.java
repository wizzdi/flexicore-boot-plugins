/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.license.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.IOperation.Access;
import com.flexicore.annotations.OperationsInside;


import com.flexicore.license.model.LicenseRequestToProduct_;
import com.wizzdi.flexicore.security.response.PaginationResponse;

import com.flexicore.license.model.LicenseRequestToProduct;
import com.flexicore.license.request.LicenseRequestToProductCreate;
import com.flexicore.license.request.LicenseRequestToProductFiltering;
import com.flexicore.license.request.LicenseRequestToProductUpdate;
import com.flexicore.license.service.LicenseRequestToProductService;
import com.flexicore.security.SecurityContextBase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.web.server.ResponseStatusException;


@RequestMapping("/licenseRequestToProducts")
@RestController
@OperationsInside

@Extension

@Tag(name = "License")


public class LicenseRequestToProductController implements Plugin {


    @Autowired

    private LicenseRequestToProductService licenseRequestToProductService;


   @PostMapping("/getAllLicenseRequestToProducts")

    @IOperation(access = Access.allow, Name = "getAllLicenseRequestToProducts", Description = "lists LicenseRequestToProducts")
    public PaginationResponse<LicenseRequestToProduct> getAllLicenseRequestToProducts(@RequestBody LicenseRequestToProductFiltering licenseRequestToProductFiltering, @RequestAttribute SecurityContextBase securityContext) {
        licenseRequestToProductService.validate(licenseRequestToProductFiltering, securityContext);
        return licenseRequestToProductService.getAllLicenseRequestToProducts(licenseRequestToProductFiltering, securityContext);

    }

  

   @PostMapping("/createLicenseRequestToProduct")

    @IOperation(access = Access.allow, Name = "Creates LicenseRequestToProduct", Description = "Creates LicenseRequestToProduct")
    public LicenseRequestToProduct createLicenseRequestToProduct(@RequestBody LicenseRequestToProductCreate licenseRequestToProductCreate, @RequestAttribute SecurityContextBase securityContext) {
        licenseRequestToProductService.validate(licenseRequestToProductCreate, securityContext);
        return licenseRequestToProductService.createLicenseRequestToProduct(licenseRequestToProductCreate, securityContext);

    }

    @PutMapping("/updateLicenseRequestToProduct")

    @IOperation(access = Access.allow, Name = "Updates LicenseRequestToProduct", Description = "Updates LicenseRequestToProduct")
    public LicenseRequestToProduct updateLicenseRequestToProduct(@RequestBody LicenseRequestToProductUpdate licenseRequestToProductUpdate, @RequestAttribute SecurityContextBase securityContext) {
        String id=licenseRequestToProductUpdate.getId();
        LicenseRequestToProduct licenseRequestToProduct=id!=null?licenseRequestToProductService.getByIdOrNull(id,LicenseRequestToProduct.class, LicenseRequestToProduct_.security,securityContext):null;
        if(licenseRequestToProduct==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No LicenseRequestToFeature with id "+id);
        }
        licenseRequestToProductUpdate.setLicenseRequestToProduct(licenseRequestToProduct);
        licenseRequestToProductService.validate(licenseRequestToProductUpdate, securityContext);
        return licenseRequestToProductService.updateLicenseRequestToProduct(licenseRequestToProductUpdate, securityContext);

    }

}
