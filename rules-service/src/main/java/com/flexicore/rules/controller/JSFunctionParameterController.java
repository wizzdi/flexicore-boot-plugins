package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.JSFunctionParameter;
import com.flexicore.rules.model.JSFunctionParameter_;
import com.flexicore.rules.request.JSFunctionParameterCreate;
import com.flexicore.rules.request.JSFunctionParameterFilter;
import com.flexicore.rules.request.JSFunctionParameterUpdate;
import com.flexicore.rules.service.JSFunctionParameterService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("JSFunctionParameter")
@Extension
@Tag(name = "JSFunctionParameter")
@OperationsInside
public class JSFunctionParameterController implements Plugin {

  @Autowired private JSFunctionParameterService jSFunctionParameterService;

  @PostMapping("createJSFunctionParameter")
  @Operation(summary = "createJSFunctionParameter", description = "Creates JSFunctionParameter")
  public JSFunctionParameter createJSFunctionParameter(
      
      @RequestBody JSFunctionParameterCreate jSFunctionParameterCreate,
      @RequestAttribute SecurityContext securityContext) {

    jSFunctionParameterService.validate(jSFunctionParameterCreate, securityContext);
    return jSFunctionParameterService.createJSFunctionParameter(
        jSFunctionParameterCreate, securityContext);
  }

  @PutMapping("updateJSFunctionParameter")
  @Operation(summary = "updateJSFunctionParameter", description = "Updates JSFunctionParameter")
  public JSFunctionParameter updateJSFunctionParameter(
      
      @RequestBody JSFunctionParameterUpdate jSFunctionParameterUpdate,
      @RequestAttribute SecurityContext securityContext) {

    String jSFunctionParameterId = jSFunctionParameterUpdate.getId();
    JSFunctionParameter jSFunctionParameter =
        jSFunctionParameterService.getByIdOrNull(
            jSFunctionParameterId,
            JSFunctionParameter.class,
            securityContext);
    if (jSFunctionParameter == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No JSFunctionParameter with id " + jSFunctionParameterId);
    }
    jSFunctionParameterUpdate.setJSFunctionParameter(jSFunctionParameter);
    jSFunctionParameterService.validate(jSFunctionParameterUpdate, securityContext);
    return jSFunctionParameterService.updateJSFunctionParameter(
        jSFunctionParameterUpdate, securityContext);
  }

  @PostMapping("getAllJSFunctionParameters")
  @Operation(summary = "getAllJSFunctionParameters", description = "lists JSFunctionParameters")
  public PaginationResponse<JSFunctionParameter> getAllJSFunctionParameters(
      
      @RequestBody JSFunctionParameterFilter jSFunctionParameterFilter,
      @RequestAttribute SecurityContext securityContext) {

    jSFunctionParameterService.validate(jSFunctionParameterFilter, securityContext);
    return jSFunctionParameterService.getAllJSFunctionParameters(
        jSFunctionParameterFilter, securityContext);
  }
}
