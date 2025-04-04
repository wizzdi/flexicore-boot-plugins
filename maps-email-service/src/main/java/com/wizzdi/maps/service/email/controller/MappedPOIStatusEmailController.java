package com.wizzdi.maps.service.email.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.email.request.SendStatusEmailRequest;
import com.wizzdi.maps.service.email.response.SendStatusEmailResponse;
import com.wizzdi.maps.service.email.service.MappedPOIStatusEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.csv.CSVFormat;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("MappedPOIStatusEmail")
@Tag(name = "MappedPOIStatusEmail")
@OperationsInside
@Extension
public class MappedPOIStatusEmailController implements Plugin, Invoker {

    @Autowired
    private MappedPOIStatusEmailService mappedPOIStatusEmailService;

    @PostMapping("sendEmail")
    @Operation(summary = "sendEmail", description = "Sends MappedPOI Status email")
    public SendStatusEmailResponse sendEmail(
            @Valid @RequestBody SendStatusEmailRequest sendStatusEmailRequest,
            @RequestAttribute SecurityContext securityContext) {
        if (sendStatusEmailRequest.getCsvFormat() == null) {
            sendStatusEmailRequest.setCsvFormat(CSVFormat.EXCEL);
        }
        return mappedPOIStatusEmailService.sendEmail(sendStatusEmailRequest, securityContext);
    }


    @Override
    public Class<?> getHandlingClass() {
        return MappedPOI.class;
    }
}
