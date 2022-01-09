package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.JsFunctionParameter;
import com.flexicore.rules.model.JsFunctionParameter_;
import com.flexicore.rules.request.JsFunctionParameterCreate;
import com.flexicore.rules.request.JsFunctionParameterFilter;
import com.flexicore.rules.request.JsFunctionParameterUpdate;
import com.flexicore.rules.service.JsFunctionParameterService;
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
@RequestMapping("JsFunctionParameter")
@Extension
@Tag(name = "JsFunctionParameter")
@OperationsInside
public class JsFunctionParameterController implements Plugin {

  @Autowired private JsFunctionParameterService jsFunctionParameterService;

  @PostMapping("createJsFunctionParameter")
  @Operation(summary = "createJsFunctionParameter", description = "Creates JsFunctionParameter")
  public JsFunctionParameter createJsFunctionParameter(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody JsFunctionParameterCreate jsFunctionParameterCreate,
      @RequestAttribute SecurityContextBase securityContext) {
    jsFunctionParameterService.validate(jsFunctionParameterCreate, securityContext);
    return jsFunctionParameterService.createJsFunctionParameter(
        jsFunctionParameterCreate, securityContext);
  }

  @Operation(summary = "updateJsFunctionParameter", description = "Updates JsFunctionParameter")
  @PutMapping("updateJsFunctionParameter")
  public JsFunctionParameter updateJsFunctionParameter(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody JsFunctionParameterUpdate jsFunctionParameterUpdate,
      @RequestAttribute SecurityContextBase securityContext) {
    String jsFunctionParameterId = jsFunctionParameterUpdate.getId();
    JsFunctionParameter jsFunctionParameter =
        jsFunctionParameterService.getByIdOrNull(
            jsFunctionParameterId,
            JsFunctionParameter.class,
            JsFunctionParameter_.security,
            securityContext);
    if (jsFunctionParameter == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No JsFunctionParameter with id " + jsFunctionParameterId);
    }
    jsFunctionParameterUpdate.setJsFunctionParameter(jsFunctionParameter);
    jsFunctionParameterService.validate(jsFunctionParameterUpdate, securityContext);
    return jsFunctionParameterService.updateJsFunctionParameter(
        jsFunctionParameterUpdate, securityContext);
  }

  @Operation(
      summary = "getAllJsFunctionParameters",
      description = "Gets All JsFunctionParameters Filtered")
  @PostMapping("getAllJsFunctionParameters")
  public PaginationResponse<JsFunctionParameter> getAllJsFunctionParameters(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody JsFunctionParameterFilter jsFunctionParameterFilter,
      @RequestAttribute SecurityContextBase securityContext) {
    jsFunctionParameterService.validate(jsFunctionParameterFilter, securityContext);
    return jsFunctionParameterService.getAllJsFunctionParameters(
        jsFunctionParameterFilter, securityContext);
  }
}
