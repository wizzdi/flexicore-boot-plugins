/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.ui.component.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.IOperation.Access;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.Protected;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.ui.component.model.UIComponent;
import com.flexicore.ui.component.request.UIComponentsRegistrationContainer;

import com.flexicore.security.SecurityContext;
import com.flexicore.ui.component.service.UIComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/uiPlugin")
@PluginInfo(version = 1)
@RequestScoped
@Component
@OperationsInside
@Protected
@Tag(name = "UI Components")
public class UIComponentRESTService implements RestServicePlugin {


    @Autowired
    @PluginInfo(version = 1)
    private UIComponentService uiPluginService;



    @POST
    @Path("registerAndGetAllowedUIComponents")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "registerAndGetAllowedUIComponents", Description = "registers components if not exists and returns allowed", relatedClazzes = {UIComponent.class})
    @Operation(summary = "registerAndGetAllowedUIComponents", description = "registers components if not exists and returns allowed")

    public List<UIComponent> registerAndGetAllowedUIComponents(@HeaderParam("authenticationkey") String authenticationkey,
                                          UIComponentsRegistrationContainer uiComponentsRegistrationContainer,
                                         @Context SecurityContext securityContext) {
        uiPluginService.validate(uiComponentsRegistrationContainer);
        return uiPluginService.registerAndGetAllowedUIComponents(uiComponentsRegistrationContainer.getComponentsToRegister(),securityContext);
    }


}
