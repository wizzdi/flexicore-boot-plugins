/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.IOperation.Access;
import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.service.service.DownloadFirmwareService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/downloadFirmware")
@Extension
@Tag(name = "DownloadFirmware")
@OperationsInside
public class DownloadFirmwareController implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(DownloadFirmwareController.class);

    @Autowired
    private DownloadFirmwareService downloadFirmwareService;



    @GetMapping("{gatewayId}/{firmwareInstallationId}/{signedId}")
    @IOperation(access = Access.allow, Name = "downloadFirmware", Description = "downloads firmware by firmware firmwareInstallationId")
    public ResponseEntity<Resource> download(@Parameter(description = "firmwareInstallationId of the FileResource Object to Download")
                                             @RequestHeader(value = "offset", defaultValue = "0") long offset,
                                             @RequestHeader(value = "size", defaultValue = "0") long size,
                                             @PathVariable("gatewayId") String gatewayId, @PathVariable("firmwareInstallationId") String firmwareInstallationId, @PathVariable("signedId") String signedId, HttpServletRequest req) {
        return downloadFirmwareService.download(offset, size,gatewayId, firmwareInstallationId,signedId, req.getRemoteAddr());

    }
}
