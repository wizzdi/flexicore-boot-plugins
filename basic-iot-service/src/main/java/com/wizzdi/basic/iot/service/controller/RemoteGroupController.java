package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.service.request.EvaluateRemoteGroupHealthRequest;
import com.wizzdi.basic.iot.service.request.RemoteGroupCreate;
import com.wizzdi.basic.iot.service.request.RemoteGroupFilter;
import com.wizzdi.basic.iot.service.request.RemoteGroupUpdate;
import com.wizzdi.basic.iot.service.response.RemoteGroupHealthSnapshot;
import com.wizzdi.basic.iot.service.service.RemoteGroupFleetHealthService;
import com.wizzdi.basic.iot.service.service.RemoteGroupService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@OperationsInside
@RequestMapping("/plugins/RemoteGroup")
@Tag(name = "RemoteGroup")
@Extension
@RestController
public class RemoteGroupController implements Plugin, Invoker {
    @Autowired
    private RemoteGroupService service;
    @Autowired
    private RemoteGroupFleetHealthService fleetHealthService;

    @PostMapping("/getAllRemoteGroups")
    @Operation(summary = "getAllRemoteGroups")
    public PaginationResponse<RemoteGroup> getAll(@RequestBody RemoteGroupFilter filter, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAll(securityContext, filter);
    }

    @PostMapping("/createRemoteGroup")
    @Operation(summary = "createRemoteGroup")
    public RemoteGroup create(@RequestBody RemoteGroupCreate create, @RequestAttribute SecurityContext securityContext) {
        service.validate(create, securityContext);
        return service.create(create, securityContext);
    }

    @PutMapping("/updateRemoteGroup")
    @Operation(summary = "updateRemoteGroup")
    public RemoteGroup update(@RequestBody RemoteGroupUpdate update, @RequestAttribute SecurityContext securityContext) {
        service.validate(update, securityContext);
        return service.update(update, securityContext);
    }

    @PostMapping("/evaluateFleetHealth")
    @Operation(summary = "evaluateFleetHealth", description = "Evaluates a RemoteGroup using the current normalized health projection of its Device and Gateway members")
    public RemoteGroupHealthSnapshot evaluateFleetHealth(@RequestBody EvaluateRemoteGroupHealthRequest request, @RequestAttribute SecurityContext securityContext) {
        if (request.getRemoteGroupId() == null || request.getRemoteGroupId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "remoteGroupId is required");
        }
        return fleetHealthService.evaluate(request.getRemoteGroupId(), securityContext);
    }

    @Override
    public Class<?> getHandlingClass() {
        return RemoteGroup.class;
    }
}
