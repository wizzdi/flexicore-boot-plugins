package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.ConnectivityChange;
import com.wizzdi.basic.iot.model.ConnectivityChange_;
import com.wizzdi.basic.iot.service.request.ConnectivityChangeCreate;
import com.wizzdi.basic.iot.service.request.ConnectivityChangeFilter;
import com.wizzdi.basic.iot.service.request.ConnectivityChangeUpdate;
import com.wizzdi.basic.iot.service.service.ConnectivityChangeService;
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

@RequestMapping("/plugins/ConnectivityChange")

@Tag(name = "ConnectivityChange")
@Extension
@RestController
public class ConnectivityChangeController implements Plugin {

    @Autowired
    private ConnectivityChangeService service;


    @Operation(summary = "getAllConnectivityChanges", description = "Lists all ConnectivityChange")
    
    @PostMapping("/getAllConnectivityChanges")
    public PaginationResponse<ConnectivityChange> getAllConnectivityChanges(

            
            @RequestBody ConnectivityChangeFilter connectivityChangeFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(connectivityChangeFilter, securityContext);
        return service.getAllConnectivityChanges(securityContext, connectivityChangeFilter);
    }


    @PostMapping("/createConnectivityChange")
    @Operation(summary = "createConnectivityChange", description = "Creates ConnectivityChange")
    
    public ConnectivityChange createConnectivityChange(
            
            @RequestBody ConnectivityChangeCreate connectivityChangeCreate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(connectivityChangeCreate, securityContext);

        return service.createConnectivityChange(connectivityChangeCreate, securityContext);
    }


    @PutMapping("/updateConnectivityChange")
    @Operation(summary = "updateConnectivityChange", description = "Updates ConnectivityChange")
    
    public ConnectivityChange updateConnectivityChange(

            
            @RequestBody ConnectivityChangeUpdate connectivityChangeUpdate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(connectivityChangeUpdate, securityContext);
        ConnectivityChange connectivityChange = service.getByIdOrNull(connectivityChangeUpdate.getId(),
                ConnectivityChange.class, ConnectivityChange_.security, securityContext);
        if (connectivityChange == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no ConnectivityChange with id "
                    + connectivityChangeUpdate.getId());
        }
        connectivityChangeUpdate.setConnectivityChange(connectivityChange);

        return service.updateConnectivityChange(connectivityChangeUpdate, securityContext);
    }
}