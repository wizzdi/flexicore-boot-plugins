package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.JsFunction;
import com.flexicore.rules.model.JsFunction_;
import com.flexicore.rules.request.JsFunctionCreate;
import com.flexicore.rules.request.JsFunctionFilter;
import com.flexicore.rules.request.JsFunctionUpdate;
import com.flexicore.rules.service.JsFunctionService;
import com.flexicore.security.SecurityContextBase;
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
@RequestMapping("JsFunction")
@Extension
@Tag(name = "JsFunction")
@OperationsInside
public class JsFunctionController implements Plugin {

  @Autowired private JsFunctionService jsFunctionService;

  @PostMapping("createJsFunction")
  @Operation(summary = "createJsFunction", description = "Creates JsFunction")
  public JsFunction createJsFunction(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody JsFunctionCreate jsFunctionCreate,
      @RequestAttribute SecurityContextBase securityContext) {
    jsFunctionService.validate(jsFunctionCreate, securityContext);
    return jsFunctionService.createJsFunction(jsFunctionCreate, securityContext);
  }

  @Operation(summary = "updateJsFunction", description = "Updates JsFunction")
  @PutMapping("updateJsFunction")
  public JsFunction updateJsFunction(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody JsFunctionUpdate jsFunctionUpdate,
      @RequestAttribute SecurityContextBase securityContext) {
    String jsFunctionId = jsFunctionUpdate.getId();
    JsFunction jsFunction =
        jsFunctionService.getByIdOrNull(
            jsFunctionId, JsFunction.class, JsFunction_.security, securityContext);
    if (jsFunction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No JsFunction with id " + jsFunctionId);
    }
    jsFunctionUpdate.setJsFunction(jsFunction);
    jsFunctionService.validate(jsFunctionUpdate, securityContext);
    return jsFunctionService.updateJsFunction(jsFunctionUpdate, securityContext);
  }

  @Operation(summary = "getAllJsFunctions", description = "Gets All JsFunctions Filtered")
  @PostMapping("getAllJsFunctions")
  public PaginationResponse<JsFunction> getAllJsFunctions(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody JsFunctionFilter jsFunctionFilter,
      @RequestAttribute SecurityContextBase securityContext) {
    jsFunctionService.validate(jsFunctionFilter, securityContext);
    return jsFunctionService.getAllJsFunctions(jsFunctionFilter, securityContext);
  }
}
