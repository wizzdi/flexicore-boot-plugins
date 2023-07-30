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


import com.wizzdi.flexicore.security.response.PaginationResponse;

import com.flexicore.license.model.LicensingProduct;
import com.flexicore.license.request.LicensingProductFiltering;
import com.flexicore.license.service.LicensingProductService;
import com.flexicore.security.SecurityContextBase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.web.bind.annotation.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;



@RequestMapping("/licensingProducts")
@RestController
@OperationsInside

@Extension

@Tag(name = "License")


public class LicensingProductController implements Plugin {


    @Autowired

    private LicensingProductService licensingProductService;


   @PostMapping("/getAllLicensingProducts")

    @IOperation(access = Access.allow, Name = "getAllLicensingProducts", Description = "lists LicensingProducts")
    public PaginationResponse<LicensingProduct> getAllLicensingProducts(@RequestBody LicensingProductFiltering licensingProductFiltering, @RequestAttribute SecurityContextBase securityContext) {
        licensingProductService.validate(licensingProductFiltering, securityContext);
        return licensingProductService.getAllLicensingProducts(licensingProductFiltering, securityContext);

    }


}
