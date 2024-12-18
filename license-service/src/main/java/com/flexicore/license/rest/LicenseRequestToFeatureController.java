/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.license.rest;

import com.flexicore.annotations.IOperation;

import com.flexicore.annotations.OperationsInside;


import com.flexicore.license.model.LicenseRequestToFeature_;
import com.wizzdi.segmantix.model.Access;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import com.wizzdi.flexicore.security.response.PaginationResponse;

import com.flexicore.license.model.LicenseRequestToFeature;
import com.flexicore.license.request.LicenseRequestToFeatureCreate;
import com.flexicore.license.request.LicenseRequestToFeatureFiltering;
import com.flexicore.license.request.LicenseRequestToFeatureUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.license.service.LicenseRequestToFeatureService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.web.server.ResponseStatusException;


@RequestMapping("/licenseRequestToFeatures")
@RestController
@OperationsInside

@Extension

@Tag(name = "License")


public class LicenseRequestToFeatureController implements Plugin {


    @Autowired

    private LicenseRequestToFeatureService licenseRequestToFeatureService;

   @PostMapping("/getAllLicenseRequestToFeatures")

    @IOperation(access = Access.allow, Name = "getAllLicenseRequestToFeatures", Description = "lists LicenseRequestToFeatures")
    public PaginationResponse<LicenseRequestToFeature> getAllLicenseRequestToFeatures(@RequestBody LicenseRequestToFeatureFiltering licenseRequestToFeatureFiltering, @RequestAttribute SecurityContext securityContext) {
        licenseRequestToFeatureService.validate(licenseRequestToFeatureFiltering, securityContext);
        return licenseRequestToFeatureService.getAllLicenseRequestToFeatures(licenseRequestToFeatureFiltering, securityContext);

    }

  

   @PostMapping("/createLicenseRequestToFeature")

    @IOperation(access = Access.allow, Name = "Creates LicenseRequestToFeature", Description = "Creates LicenseRequestToFeature")
    public LicenseRequestToFeature createLicenseRequestToFeature(@RequestBody LicenseRequestToFeatureCreate licenseRequestToFeatureCreate, @RequestAttribute SecurityContext securityContext) {
        licenseRequestToFeatureService.validate(licenseRequestToFeatureCreate, securityContext);
        return licenseRequestToFeatureService.createLicenseRequestToFeature(licenseRequestToFeatureCreate, securityContext);

    }

    @PutMapping("/updateLicenseRequestToFeature")

    @IOperation(access = Access.allow, Name = "Updates LicenseRequestToFeature", Description = "Updates LicenseRequestToFeature")
    public LicenseRequestToFeature updateLicenseRequestToFeature(@RequestBody LicenseRequestToFeatureUpdate licenseRequestToFeatureUpdate, @RequestAttribute SecurityContext securityContext) {
        String id=licenseRequestToFeatureUpdate.getId();
        LicenseRequestToFeature licenseRequestToFeature=id!=null?licenseRequestToFeatureService.getByIdOrNull(id,LicenseRequestToFeature.class, securityContext):null;
        if(licenseRequestToFeature==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No LicenseRequestToFeature with id "+id);
        }
        licenseRequestToFeatureUpdate.setLicenseRequestToFeature(licenseRequestToFeature);
        licenseRequestToFeatureService.validate(licenseRequestToFeatureUpdate, securityContext);
        return licenseRequestToFeatureService.updateLicenseRequestToFeature(licenseRequestToFeatureUpdate, securityContext);

    }

}
