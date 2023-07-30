/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.ui.component.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.component.model.UIComponent;
import com.flexicore.ui.component.request.UIComponentsRegistrationContainer;
import com.flexicore.ui.component.service.UIComponentService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/uiComponent")
@OperationsInside
@RestController
@Tag(name = "UI Components")
@Extension
public class UIComponentController implements Plugin {


    @Autowired
    
    private UIComponentService uiPluginService;



    @PostMapping("/registerAndGetAllowedUIComponents")
    @Operation(summary = "registerAndGetAllowedUIComponents", description = "registers components if not exists and returns allowed")

    public List<UIComponent> registerAndGetAllowedUIComponents(@RequestHeader("authenticationkey") String authenticationkey,
                                         @RequestBody UIComponentsRegistrationContainer uiComponentsRegistrationContainer,
                                                               @RequestAttribute SecurityContextBase securityContext) {
        uiPluginService.validate(uiComponentsRegistrationContainer);
        return uiPluginService.registerAndGetAllowedUIComponents(uiComponentsRegistrationContainer.getComponentsToRegister(),securityContext);
    }


}
