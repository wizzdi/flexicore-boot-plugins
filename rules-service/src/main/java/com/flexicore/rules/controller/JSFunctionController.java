package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.JSFunction;
import com.flexicore.rules.model.JSFunction_;
import com.flexicore.rules.request.JSFunctionCreate;
import com.flexicore.rules.request.JSFunctionFilter;
import com.flexicore.rules.request.JSFunctionUpdate;
import com.flexicore.rules.service.JSFunctionService;
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
@RequestMapping("JSFunction")
@Extension
@Tag(name = "JSFunction")
@OperationsInside
public class JSFunctionController implements Plugin {

  @Autowired private JSFunctionService jSFunctionService;

  @PostMapping("getAllJSFunctions")
  @Operation(summary = "getAllJSFunctions", description = "lists JSFunctions")
  public PaginationResponse<JSFunction> getAllJSFunctions(
      
      @RequestBody JSFunctionFilter jSFunctionFilter,
      @RequestAttribute SecurityContext securityContext) {

    jSFunctionService.validate(jSFunctionFilter, securityContext);
    return jSFunctionService.getAllJSFunctions(jSFunctionFilter, securityContext);
  }

  @PostMapping("createJSFunction")
  @Operation(summary = "createJSFunction", description = "Creates JSFunction")
  public JSFunction createJSFunction(
      
      @RequestBody JSFunctionCreate jSFunctionCreate,
      @RequestAttribute SecurityContext securityContext) {

    jSFunctionService.validate(jSFunctionCreate, securityContext);
    return jSFunctionService.createJSFunction(jSFunctionCreate, securityContext);
  }

  @PutMapping("updateJSFunction")
  @Operation(summary = "updateJSFunction", description = "Updates JSFunction")
  public JSFunction updateJSFunction(
      
      @RequestBody JSFunctionUpdate jSFunctionUpdate,
      @RequestAttribute SecurityContext securityContext) {

    String jSFunctionId = jSFunctionUpdate.getId();
    JSFunction jSFunction =
        jSFunctionService.getByIdOrNull(
            jSFunctionId, JSFunction.class,  securityContext);
    if (jSFunction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No JSFunction with id " + jSFunctionId);
    }
    jSFunctionUpdate.setJSFunction(jSFunction);
    jSFunctionService.validate(jSFunctionUpdate, securityContext);
    return jSFunctionService.updateJSFunction(jSFunctionUpdate, securityContext);
  }
}
