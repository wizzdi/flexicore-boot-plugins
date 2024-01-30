package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.request.ClearLogRequest;
import com.flexicore.rules.response.FixMissingLogsResponse;
import com.flexicore.rules.service.ScenarioLogService;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ScenarioLog")
@Extension
@Tag(name = "ScenarioLog")
@OperationsInside
public class ScenarioLogController implements Plugin {

    @Autowired
    private ScenarioLogService scenarioLogService;

    @PostMapping("clearLog")
    @Operation(summary = "clearLog", description = "clears Scenario log")
    public void clearLog(
            
            @RequestBody ClearLogRequest scenarioCreate,
            @RequestAttribute SecurityContextBase securityContext) {
        scenarioLogService.validate(scenarioCreate, securityContext);
        scenarioLogService.clearLog(scenarioCreate, securityContext);
    }

    @PostMapping("fixMissingLogFiles")
    @Operation(summary = "fixMissingLogFiles", description = "fixesMissingLogFiles")
    public FixMissingLogsResponse fixMissingLogFiles(
            @RequestAttribute SecurityContextBase securityContext) {
        return scenarioLogService.fixMissingLogFiles(securityContext);
    }

}
