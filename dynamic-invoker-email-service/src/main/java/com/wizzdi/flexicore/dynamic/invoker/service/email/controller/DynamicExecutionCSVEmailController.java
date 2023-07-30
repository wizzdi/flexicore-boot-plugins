package com.wizzdi.flexicore.dynamic.invoker.service.email.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.interfaces.dynamic.Invoker;
import com.flexicore.security.SecurityContext;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.service.impl.DynamicInvokersService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.dynamic.invoker.service.email.request.SendDynamicExecutionRequest;
import com.wizzdi.flexicore.dynamic.invoker.service.email.response.SendDynamicEmailResponse;
import com.wizzdi.flexicore.dynamic.invoker.service.email.service.DynamicInvokerCSVEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("DynamicExecutionCSVEmail")
@Tag(name = "DynamicExecutionCSVEmail")
@OperationsInside
@Extension
public class DynamicExecutionCSVEmailController implements Plugin, Invoker {

    @Autowired
    private DynamicInvokerCSVEmailService invokerCSVEmailService;
    @Autowired
    private DynamicInvokersService dynamicInvokersService;

    @PostMapping("sendEmail")
    @Operation(summary = "sendEmail", description = "Sends CSV email")
    public SendDynamicEmailResponse sendEmail(
            @Valid @RequestBody SendDynamicExecutionRequest sendDynamicExecutionRequest,
            @RequestAttribute SecurityContextBase securityContext) {
        dynamicInvokersService.validateExportDynamicExecution(sendDynamicExecutionRequest.getExportDynamicExecution(), (SecurityContext) securityContext);
        return invokerCSVEmailService.sendEmail(sendDynamicExecutionRequest, (SecurityContext) securityContext);
    }


    @Override
    public Class<?> getHandlingClass() {
        return DynamicExecution.class;
    }
}
