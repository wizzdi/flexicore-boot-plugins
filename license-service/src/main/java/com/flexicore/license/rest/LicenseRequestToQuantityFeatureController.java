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


import com.flexicore.license.model.LicenseRequestToQuantityFeature_;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import com.wizzdi.flexicore.security.response.PaginationResponse;

import com.flexicore.license.model.LicenseRequestToQuantityFeature;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureCreate;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureFiltering;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureUpdate;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.license.service.LicenseRequestToQuantityFeatureService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.web.server.ResponseStatusException;


@RequestMapping("/licenseRequestToQuantityFeatures")
@RestController
@OperationsInside

@Extension

@Tag(name = "License")


public class LicenseRequestToQuantityFeatureController implements Plugin {


    @Autowired

    private LicenseRequestToQuantityFeatureService licenseRequestToQuantityFeatureService;


   @PostMapping("/getAllLicenseRequestToQuantityFeatures")

    @IOperation(access = Access.allow, Name = "getAllLicenseRequestToQuantityFeatures", Description = "lists LicenseRequestToQuantityFeatures")
    public PaginationResponse<LicenseRequestToQuantityFeature> getAllLicenseRequestToQuantityFeatures(@RequestBody LicenseRequestToQuantityFeatureFiltering licenseRequestToQuantityFeatureFiltering, @RequestAttribute SecurityContextBase securityContext) {
        licenseRequestToQuantityFeatureService.validate(licenseRequestToQuantityFeatureFiltering, securityContext);
        return licenseRequestToQuantityFeatureService.getAllLicenseRequestToQuantityFeatures(licenseRequestToQuantityFeatureFiltering, securityContext);

    }

  

   @PostMapping("/createLicenseRequestToQuantityFeature")

    @IOperation(access = Access.allow, Name = "Creates LicenseRequestToQuantityFeature", Description = "Creates LicenseRequestToQuantityFeature")
    public LicenseRequestToQuantityFeature createLicenseRequestToQuantityFeature(@RequestBody LicenseRequestToQuantityFeatureCreate licenseRequestToQuantityFeatureCreate, @RequestAttribute SecurityContextBase securityContext) {
        licenseRequestToQuantityFeatureService.validate(licenseRequestToQuantityFeatureCreate, securityContext);
        return licenseRequestToQuantityFeatureService.createLicenseRequestToQuantityFeature(licenseRequestToQuantityFeatureCreate, securityContext);

    }

    @PutMapping("/updateLicenseRequestToQuantityFeature")

    @IOperation(access = Access.allow, Name = "Updates LicenseRequestToQuantityFeature", Description = "Updates LicenseRequestToQuantityFeature")
    public LicenseRequestToQuantityFeature updateLicenseRequestToQuantityFeature(@RequestBody LicenseRequestToQuantityFeatureUpdate licenseRequestToQuantityFeatureUpdate, @RequestAttribute SecurityContextBase securityContext) {
        String id=licenseRequestToQuantityFeatureUpdate.getId();
        LicenseRequestToQuantityFeature licenseRequestToQuantityFeature=id!=null?licenseRequestToQuantityFeatureService.getByIdOrNull(id,LicenseRequestToQuantityFeature.class, LicenseRequestToQuantityFeature_.security,securityContext):null;
        if(licenseRequestToQuantityFeature==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No LicenseRequestToQuantityFeature with id "+id);
        }
        licenseRequestToQuantityFeatureUpdate.setLicenseRequestToQuantityFeature(licenseRequestToQuantityFeature);
        licenseRequestToQuantityFeatureService.validate(licenseRequestToQuantityFeatureUpdate, securityContext);
        return licenseRequestToQuantityFeatureService.updateLicenseRequestToQuantityFeature(licenseRequestToQuantityFeatureUpdate, securityContext);

    }

}
