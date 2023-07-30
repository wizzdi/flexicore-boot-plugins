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


import com.flexicore.license.model.LicenseRequest_;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import com.wizzdi.flexicore.security.response.PaginationResponse;

import com.flexicore.license.model.LicenseRequest;
import com.flexicore.license.request.LicenseRequestCreate;
import com.flexicore.license.request.LicenseRequestFiltering;
import com.flexicore.license.request.LicenseRequestUpdate;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.license.service.LicenseRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.web.server.ResponseStatusException;


@RequestMapping("/licenseRequests")
@RestController
@OperationsInside

@Extension

@Tag(name = "License")


public class LicenseRequestController implements Plugin {


    @Autowired

    private LicenseRequestService licenseRequestService;


   @PostMapping("/getAllLicenseRequests")

    @IOperation(access = Access.allow, Name = "getAllLicenseRequests", Description = "lists LicenseRequests")
    public PaginationResponse<LicenseRequest> getAllLicenseRequests(@RequestBody LicenseRequestFiltering licenseRequestFiltering, @RequestAttribute SecurityContextBase securityContext) {
        licenseRequestService.validate(licenseRequestFiltering, securityContext);
        return licenseRequestService.getAllLicenseRequests(licenseRequestFiltering, securityContext);

    }

  

   @PostMapping("/createLicenseRequest")

    @IOperation(access = Access.allow, Name = "Creates LicenseRequest", Description = "Creates LicenseRequest")
    public LicenseRequest createLicenseRequest(@RequestBody LicenseRequestCreate licenseRequestCreate, @RequestAttribute SecurityContextBase securityContext) {
        licenseRequestService.validateCreate(licenseRequestCreate,securityContext);
        return licenseRequestService.createLicenseRequest(licenseRequestCreate, securityContext);

    }

    @PutMapping("/updateLicenseRequest")

    @IOperation(access = Access.allow, Name = "Updates LicenseRequest", Description = "Updates LicenseRequest")
    public LicenseRequest updateLicenseRequest(@RequestBody LicenseRequestUpdate licenseRequestUpdate, @RequestAttribute SecurityContextBase securityContext) {
        String id=licenseRequestUpdate.getId();
        LicenseRequest licenseRequest=id!=null?licenseRequestService.getByIdOrNull(id,LicenseRequest.class, LicenseRequest_.security,securityContext):null;
        if(licenseRequest==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No License request with id "+id);
        }
        licenseRequestUpdate.setLicenseRequest(licenseRequest);
        licenseRequestService.validate(licenseRequestUpdate, securityContext);
        return licenseRequestService.updateLicenseRequest(licenseRequestUpdate, securityContext);

    }

}
