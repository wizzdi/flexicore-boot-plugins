package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.request.EvaluateRemoteHealthRequest;
import com.wizzdi.basic.iot.service.response.RemoteHealthSnapshot;
import com.wizzdi.basic.iot.service.service.RemoteHealthEvaluationService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@OperationsInside
@RequestMapping("/plugins/RemoteHealth")
@Tag(name = "RemoteHealth")
@Extension
@RestController
public class RemoteHealthController implements Plugin, Invoker {
    @Autowired
    private RemoteHealthEvaluationService service;

    @PostMapping("/evaluateRemoteHealth")
    @Operation(summary = "evaluateRemoteHealth")
    public RemoteHealthSnapshot evaluate(@RequestBody EvaluateRemoteHealthRequest request, @RequestAttribute SecurityContext securityContext) {
        service.validate(request, securityContext);
        return service.evaluate(request.getRemote(), OffsetDateTime.now());
    }

    @Override
    public Class<?> getHandlingClass() { return Remote.class; }
}
