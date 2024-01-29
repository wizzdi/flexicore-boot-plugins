package com.wizzdi.flexicore.dynamic.invoker.service.email.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.dynamic.invoker.service.email.request.SendDynamicExecutionRequest;
import com.wizzdi.flexicore.dynamic.invoker.service.email.response.SendDynamicEmailResponse;
import com.wizzdi.flexicore.dynamic.invoker.service.email.service.DynamicInvokerEmailService;
import com.wizzdi.flexicore.dynamic.invoker.service.export.service.DynamicInvokerExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("DynamicExecutionEmail")
@Tag(name = "DynamicExecutionEmail")
@OperationsInside
@Extension
public class DynamicExecutionEmailController implements Plugin, Invoker {

    @Autowired
    private DynamicInvokerEmailService dynamicInvokerEmailService;
    @Autowired
    private DynamicInvokerExportService dynamicInvokerExportService;

    @PostMapping("sendEmail")
    @Operation(summary = "sendEmail", description = "Sends MappedPOI Status History email")
    public SendDynamicEmailResponse sendEmail(
            @Valid @RequestBody SendDynamicExecutionRequest sendDynamicExecutionRequest,
            @RequestAttribute SecurityContextBase securityContext) {
        dynamicInvokerExportService.validateExportDynamicExecution(sendDynamicExecutionRequest.getExportDynamicExecution(), securityContext);

        return dynamicInvokerEmailService.sendEmail(sendDynamicExecutionRequest, securityContext);
    }


    @Override
    public Class<?> getHandlingClass() {
        return DynamicExecution.class;
    }
}
