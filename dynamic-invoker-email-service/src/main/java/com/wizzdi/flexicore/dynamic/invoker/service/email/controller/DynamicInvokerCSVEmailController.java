package com.wizzdi.flexicore.dynamic.invoker.service.email.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.dynamic.invoker.service.email.request.SendDynamicInvokerRequest;
import com.wizzdi.flexicore.dynamic.invoker.service.email.response.SendDynamicEmailResponse;
import com.wizzdi.flexicore.dynamic.invoker.service.email.service.DynamicInvokerCSVEmailService;
import com.wizzdi.flexicore.dynamic.invoker.service.export.service.DynamicInvokerExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("DynamicInvokerCSVEmail")
@Tag(name = "DynamicInvokerCSVEmail")
@OperationsInside
@Extension
public class DynamicInvokerCSVEmailController implements Plugin, Invoker {

    @Autowired
    private DynamicInvokerCSVEmailService invokerCSVEmailService;
    @Autowired
    private DynamicInvokerExportService dynamicInvokerExportService;

    @PostMapping("sendEmail")
    @Operation(summary = "sendEmail", description = "Sends CSV email")
    public SendDynamicEmailResponse sendEmail(
            @Valid @RequestBody SendDynamicInvokerRequest sendDynamicExecutionRequest,
            @RequestAttribute SecurityContext securityContext) {
        dynamicInvokerExportService.validateExportDynamicInvoker(sendDynamicExecutionRequest.getExportDynamicInvoker(), securityContext);
        return invokerCSVEmailService.sendEmail(sendDynamicExecutionRequest, securityContext);
    }


    @Override
    public Class<?> getHandlingClass() {
        return DynamicExecution.class;
    }
}
