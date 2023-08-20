package com.wizzdi.maps.service.email.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.interfaces.dynamic.Invoker;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.email.request.SendStatusHistoryEmailRequest;
import com.wizzdi.maps.service.email.response.SendStatusEmailResponse;
import com.wizzdi.maps.service.email.service.MappedPOIStatusHistoryEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.csv.CSVFormat;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("MappedPOIStatusHistoryEmail")
@Tag(name = "MappedPOIStatusHistoryEmail")
@OperationsInside
@Extension
public class MappedPOIStatusHistoryEmailController implements Plugin, Invoker {

    @Autowired
    private MappedPOIStatusHistoryEmailService mappedPOIStatusEmailService;

    @PostMapping("sendEmail")
    @Operation(summary = "sendEmail", description = "Sends MappedPOI Status History email")
    public SendStatusEmailResponse sendEmail(
            @Valid @RequestBody SendStatusHistoryEmailRequest sendStatusEmailRequest,
            @RequestAttribute SecurityContextBase securityContext) {
        if(sendStatusEmailRequest.getStatusHistoryFilter().getStartDate()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"statusHistoryFilter.startDate must be provided");
        }
        if(sendStatusEmailRequest.getStatusHistoryFilter().getEndDate()==null){
            sendStatusEmailRequest.getStatusHistoryFilter().setEndDate(OffsetDateTime.now());
        }
        if(sendStatusEmailRequest.getCsvFormat()==null){
            sendStatusEmailRequest.setCsvFormat(CSVFormat.EXCEL);
        }
        return mappedPOIStatusEmailService.sendEmail(sendStatusEmailRequest, securityContext);
    }


    @Override
    public Class<?> getHandlingClass() {
        return MappedPOI.class;
    }
}
