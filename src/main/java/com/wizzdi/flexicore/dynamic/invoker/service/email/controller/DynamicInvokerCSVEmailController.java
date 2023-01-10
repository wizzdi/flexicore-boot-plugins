package com.wizzdi.flexicore.dynamic.invoker.service.email.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.interfaces.dynamic.Invoker;
import com.flexicore.security.SecurityContext;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.service.impl.DynamicInvokersService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.dynamic.invoker.service.email.response.SendStatusEmailResponse;
import com.wizzdi.flexicore.dynamic.invoker.service.email.request.SendDynamicInvokerRequest;
import com.wizzdi.flexicore.dynamic.invoker.service.email.service.DynamicInvokerCSVEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("DynamicInvokerCSVEmail")
@Tag(name = "DynamicInvokerCSVEmail")
@OperationsInside
@Extension
public class DynamicInvokerCSVEmailController implements Plugin, Invoker {

    @Autowired
    private DynamicInvokerCSVEmailService invokerCSVEmailService;
    @Autowired
    private DynamicInvokersService dynamicInvokersService;

    @PostMapping("sendEmail")
    @Operation(summary = "sendEmail", description = "Sends CSV email")
    public SendStatusEmailResponse sendEmail(
            @Valid @RequestBody SendDynamicInvokerRequest sendDynamicInvokerRequest,
            @RequestAttribute SecurityContextBase securityContext) {
        dynamicInvokersService.validateExportDynamicExecution(sendDynamicInvokerRequest.getExportDynamicExecution(), (SecurityContext) securityContext);
        return invokerCSVEmailService.sendEmail(sendDynamicInvokerRequest, (SecurityContext) securityContext);
    }


    @Override
    public Class<?> getHandlingClass() {
        return DynamicExecution.class;
    }
}
