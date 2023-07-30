/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.license.app;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.IOperation.Access;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.license.annotations.HasFeature;
import com.flexicore.license.annotations.HasFeatures;
import com.flexicore.license.model.LicenseRequest;
import com.flexicore.license.request.LicenseRequestFiltering;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/licenseTest")
@RestController
@OperationsInside

@Extension

@Tag(name = "License")
@HasFeatures(features = {@HasFeature(canonicalName = "license.test",productCanonicalName = "product.test")})

public class LicensedController implements Plugin {




   @GetMapping("/test")

    @IOperation(access = Access.allow, Name = "test", Description = "test")
    public String test( @RequestAttribute SecurityContextBase securityContext) {
     return "test";

    }

  


}
