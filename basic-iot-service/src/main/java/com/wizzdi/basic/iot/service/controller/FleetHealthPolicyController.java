package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.FleetHealthPolicy;
import com.wizzdi.basic.iot.service.request.FleetHealthPolicyCreate;
import com.wizzdi.basic.iot.service.request.FleetHealthPolicyFilter;
import com.wizzdi.basic.iot.service.request.FleetHealthPolicyUpdate;
import com.wizzdi.basic.iot.service.service.FleetHealthPolicyService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@OperationsInside
@RequestMapping("/plugins/FleetHealthPolicy")
@Tag(name = "FleetHealthPolicy")
@Extension
@RestController
public class FleetHealthPolicyController implements Plugin, Invoker {
    @Autowired
    private FleetHealthPolicyService service;

    @PostMapping("/getAllFleetHealthPolicies")
    @Operation(summary = "getAllFleetHealthPolicies")
    public PaginationResponse<FleetHealthPolicy> getAll(@RequestBody FleetHealthPolicyFilter filter, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAll(securityContext, filter);
    }

    @PostMapping("/createFleetHealthPolicy")
    @Operation(summary = "createFleetHealthPolicy")
    public FleetHealthPolicy create(@RequestBody FleetHealthPolicyCreate create, @RequestAttribute SecurityContext securityContext) {
        service.validate(create, securityContext);
        return service.create(create, securityContext);
    }

    @PutMapping("/updateFleetHealthPolicy")
    @Operation(summary = "updateFleetHealthPolicy")
    public FleetHealthPolicy update(@RequestBody FleetHealthPolicyUpdate update, @RequestAttribute SecurityContext securityContext) {
        service.validate(update, securityContext);
        return service.update(update, securityContext);
    }

    @Override
    public Class<?> getHandlingClass() {
        return FleetHealthPolicy.class;
    }
}
