package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.StateSchema;
import com.wizzdi.basic.iot.model.StateSchema_;
import com.wizzdi.basic.iot.service.request.StateSchemaCreate;
import com.wizzdi.basic.iot.service.request.StateSchemaFilter;
import com.wizzdi.basic.iot.service.request.StateSchemaUpdate;
import com.wizzdi.basic.iot.service.service.StateSchemaService;
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

@RequestMapping("/plugins/StateSchema")

@Tag(name = "StateSchema")
@Extension
@RestController
public class StateSchemaController implements Plugin {

    @Autowired
    private StateSchemaService service;


    @Operation(summary = "getAllStateSchemas", description = "Lists all StateSchema")
    
    @PostMapping("/getAllStateSchemas")
    public PaginationResponse<StateSchema> getAllStateSchemas(

            
            @RequestBody StateSchemaFilter stateSchemaFilter, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(stateSchemaFilter, securityContext);
        return service.getAllStateSchemas(securityContext, stateSchemaFilter);
    }


    @PostMapping("/createStateSchema")
    @Operation(summary = "createStateSchema", description = "Creates StateSchema")
    
    public StateSchema createStateSchema(
            
            @RequestBody StateSchemaCreate stateSchemaCreate,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(stateSchemaCreate, securityContext);

        return service.createStateSchema(stateSchemaCreate, securityContext);
    }


    @PutMapping("/updateStateSchema")
    @Operation(summary = "updateStateSchema", description = "Updates StateSchema")
    
    public StateSchema updateStateSchema(

            
            @RequestBody StateSchemaUpdate stateSchemaUpdate,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(stateSchemaUpdate, securityContext);
        StateSchema stateSchema = service.getByIdOrNull(stateSchemaUpdate.getId(),
                StateSchema.class,  securityContext);
        if (stateSchema == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no StateSchema with id "
                    + stateSchemaUpdate.getId());
        }
        stateSchemaUpdate.setStateSchema(stateSchema);

        return service.updateStateSchema(stateSchemaUpdate, securityContext);
    }
}
