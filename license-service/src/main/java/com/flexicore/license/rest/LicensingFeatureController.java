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

import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.request.LicensingFeatureFiltering;
import com.flexicore.license.service.LicensingFeatureService;
import com.flexicore.security.SecurityContextBase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;



import org.springframework.web.bind.annotation.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;



@RequestMapping("/licensingFeatures")
@RestController
@OperationsInside

@Extension

@Tag(name = "License")


public class LicensingFeatureController implements Plugin {


    @Autowired

    private LicensingFeatureService licensingFeatureService;

   @PostMapping("/getAllLicensingFeatures")

    @IOperation(access = Access.allow, Name = "getAllLicensingFeatures", Description = "lists LicensingFeatures")
    public PaginationResponse<LicensingFeature> getAllLicensingFeatures(@RequestBody LicensingFeatureFiltering licensingFeatureFiltering, @RequestAttribute SecurityContextBase securityContext) {
        licensingFeatureService.validate(licensingFeatureFiltering, securityContext);
        return licensingFeatureService.getAllLicensingFeatures(licensingFeatureFiltering, securityContext);

    }


}
