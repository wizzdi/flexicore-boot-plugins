package com.wizzdi.flexicore.dynamic.invoker.service.export.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.dynamic.invoker.service.export.request.ExportDynamicInvoker;
import com.wizzdi.flexicore.dynamic.invoker.service.export.service.DynamicInvokerExportService;
import com.wizzdi.flexicore.file.model.FileResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("DynamicInvokerCSV")
@Tag(name = "DynamicInvokerCSV")
@OperationsInside
@Extension
public class DynamicInvokerCSVController implements Plugin, Invoker {


    @Autowired
    private DynamicInvokerExportService dynamicInvokerExportService;

    @PostMapping
    @Operation(summary = "exportDynamicInvokerToCSV", description = "Export CSV")
    public FileResource exportDynamicInvokerToCSV(
            @Valid @RequestBody ExportDynamicInvoker exportDynamicInvoker,
            @RequestAttribute SecurityContext securityContext) {
        dynamicInvokerExportService.validateExportDynamicInvoker(exportDynamicInvoker, securityContext);
        return dynamicInvokerExportService.exportDynamicInvokerToCSV(exportDynamicInvoker, securityContext);
    }


    @Override
    public Class<?> getHandlingClass() {
        return DynamicExecution.class;
    }
}
