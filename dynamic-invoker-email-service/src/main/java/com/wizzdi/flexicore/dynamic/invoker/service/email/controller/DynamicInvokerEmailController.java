package com.wizzdi.flexicore.dynamic.invoker.service.email.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.interfaces.dynamic.Invoker;
import com.flexicore.security.SecurityContext;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.service.impl.DynamicInvokersService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.dynamic.invoker.service.email.request.SendDynamicInvokerRequest;
import com.wizzdi.flexicore.dynamic.invoker.service.email.request.SendEmailPlainRequest;
import com.wizzdi.flexicore.dynamic.invoker.service.email.response.SendDynamicEmailResponse;
import com.wizzdi.flexicore.dynamic.invoker.service.email.service.DynamicInvokerEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("DynamicInvokerEmail")
@Tag(name = "DynamicInvokerEmail")
@OperationsInside
@Extension
public class DynamicInvokerEmailController implements Plugin, Invoker {

    @Autowired
    private DynamicInvokerEmailService dynamicInvokerEmailService;
    @Autowired
    private DynamicInvokersService dynamicInvokersService;

    @PostMapping("sendEmail")
    @Operation(summary = "sendEmail", description = "Sends MappedPOI Status History email")
    public SendDynamicEmailResponse sendEmail(
            @Valid @RequestBody SendDynamicInvokerRequest sendDynamicInvokerRequest,
            @RequestAttribute SecurityContextBase securityContext) {
        dynamicInvokersService.validateExportDynamicInvoker(sendDynamicInvokerRequest.getExportDynamicInvoker(), (SecurityContext) securityContext);

        return dynamicInvokerEmailService.sendEmail(sendDynamicInvokerRequest, securityContext);
    }

    @PostMapping("sendEmailPlain")
    @Operation(summary = "sendEmailPlain", description = "Sends email plain")
    public SendDynamicEmailResponse sendEmailPlain(
            @Valid @RequestBody SendEmailPlainRequest sendDynamicInvokerRequest,
            @RequestAttribute SecurityContextBase securityContext) {

        return dynamicInvokerEmailService.sendEmailPlain(sendDynamicInvokerRequest, securityContext);
    }


    @Override
    public Class<?> getHandlingClass() {
        return DynamicExecution.class;
    }
}
