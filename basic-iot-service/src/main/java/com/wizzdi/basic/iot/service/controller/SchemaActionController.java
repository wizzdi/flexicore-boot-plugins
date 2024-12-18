package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
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

            
            @RequestBody SchemaActionFilter schemaActionFilter, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(schemaActionFilter, securityContext);
        return service.getAllSchemaActions(securityContext, schemaActionFilter);
    }


    @PostMapping("/createSchemaAction")
    @Operation(summary = "createSchemaAction", description = "Creates SchemaAction")
    
    public SchemaAction createSchemaAction(
            
            @RequestBody SchemaActionCreate schemaActionCreate,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(schemaActionCreate, securityContext);

        return service.createSchemaAction(schemaActionCreate, securityContext);
    }


    @PutMapping("/updateSchemaAction")
    @Operation(summary = "updateSchemaAction", description = "Updates SchemaAction")
    
    public SchemaAction updateSchemaAction(

            
            @RequestBody SchemaActionUpdate schemaActionUpdate,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(schemaActionUpdate, securityContext);
        SchemaAction schemaAction = service.getByIdOrNull(schemaActionUpdate.getId(),
                SchemaAction.class,  securityContext);
        if (schemaAction == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no SchemaAction with id "
                    + schemaActionUpdate.getId());
        }
        schemaActionUpdate.setSchemaAction(schemaAction);

        return service.updateSchemaAction(schemaActionUpdate, securityContext);
    }
}
