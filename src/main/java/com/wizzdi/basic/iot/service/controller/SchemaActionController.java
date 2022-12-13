package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.SchemaAction;
import com.wizzdi.basic.iot.model.SchemaAction_;
import com.wizzdi.basic.iot.service.request.SchemaActionCreate;
import com.wizzdi.basic.iot.service.request.SchemaActionFilter;
import com.wizzdi.basic.iot.service.request.SchemaActionUpdate;
import com.wizzdi.basic.iot.service.service.SchemaActionService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/SchemaAction")

@Tag(name = "SchemaAction")
@Extension
@RestController
public class SchemaActionController implements Plugin {

    @Autowired
    private SchemaActionService service;


    @Operation(summary = "getAllSchemaActions", description = "Lists all SchemaAction")
    
    @PostMapping("/getAllSchemaActions")
    public PaginationResponse<SchemaAction> getAllSchemaActions(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody SchemaActionFilter schemaActionFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(schemaActionFilter, securityContext);
        return service.getAllSchemaActions(securityContext, schemaActionFilter);
    }


    @PostMapping("/createSchemaAction")
    @Operation(summary = "createSchemaAction", description = "Creates SchemaAction")
    
    public SchemaAction createSchemaAction(
            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody SchemaActionCreate schemaActionCreate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(schemaActionCreate, securityContext);

        return service.createSchemaAction(schemaActionCreate, securityContext);
    }


    @PutMapping("/updateSchemaAction")
    @Operation(summary = "updateSchemaAction", description = "Updates SchemaAction")
    
    public SchemaAction updateSchemaAction(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody SchemaActionUpdate schemaActionUpdate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(schemaActionUpdate, securityContext);
        SchemaAction schemaAction = service.getByIdOrNull(schemaActionUpdate.getId(),
                SchemaAction.class, SchemaAction_.security, securityContext);
        if (schemaAction == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no SchemaAction with id "
                    + schemaActionUpdate.getId());
        }
        schemaActionUpdate.setSchemaAction(schemaAction);

        return service.updateSchemaAction(schemaActionUpdate, securityContext);
    }
}