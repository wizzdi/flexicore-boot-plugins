package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.Remote_;
import com.wizzdi.basic.iot.service.request.RemoteCreate;
import com.wizzdi.basic.iot.service.request.RemoteFilter;
import com.wizzdi.basic.iot.service.request.RemoteUpdate;
import com.wizzdi.basic.iot.service.response.FixRemotesResponse;
import com.wizzdi.basic.iot.service.service.RemoteService;
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

@RequestMapping("/plugins/Remote")

@Tag(name = "Remote")
@Extension
@RestController
public class RemoteController implements Plugin {

    @Autowired
    private RemoteService service;


    @Operation(summary = "getAllRemotes", description = "Lists all Remote")
    
    @PostMapping("/getAllRemotes")
    public PaginationResponse<Remote> getAllRemotes(

            
            @RequestBody RemoteFilter remoteFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(remoteFilter, securityContext);
        return service.getAllRemotes(securityContext, remoteFilter);
    }
    @Operation(summary = "fixRemoteMapIcons", description = "fixes all Remotes map icons")

    @PostMapping("/fixRemoteMapIcons")
    public FixRemotesResponse fixRemoteMapIcons(


            @RequestBody RemoteFilter remoteFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(remoteFilter, securityContext);
        return service.fixRemoteMapIcons(securityContext, remoteFilter);
    }


    @PostMapping("/createRemote")
    @Operation(summary = "createRemote", description = "Creates Remote")
    
    public Remote createRemote(
            
            @RequestBody RemoteCreate remoteCreate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(remoteCreate, securityContext);

        return service.createRemote(remoteCreate, securityContext);
    }


    @PutMapping("/updateRemote")
    @Operation(summary = "updateRemote", description = "Updates Remote")
    
    public Remote updateRemote(

            
            @RequestBody RemoteUpdate remoteUpdate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(remoteUpdate, securityContext);
        Remote remote = service.getByIdOrNull(remoteUpdate.getId(),
                Remote.class, Remote_.security, securityContext);
        if (remote == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Remote with id "
                    + remoteUpdate.getId());
        }
        remoteUpdate.setRemote(remote);

        return service.updateRemote(remoteUpdate, securityContext);
    }
}