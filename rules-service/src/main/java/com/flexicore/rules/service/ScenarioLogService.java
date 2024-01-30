package com.flexicore.rules.service;

import com.flexicore.model.SecuredBasic_;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.request.ClearLogRequest;
import com.flexicore.rules.request.ScenarioFilter;
import com.flexicore.rules.request.ScenarioTriggerFilter;
import com.flexicore.rules.response.FixMissingLogsResponse;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.file.model.FileResource;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
@Extension
public class ScenarioLogService implements Plugin {
    @Autowired
    private ScenarioService scenarioService;
    @Autowired
    private ScenarioTriggerService scenarioTriggerService;
    @Autowired
    private LogFileCreatorService logFileCreatorService;

    public void validate(ClearLogRequest clearLogRequest, SecurityContextBase securityContext) {
        String scenarioId = clearLogRequest.getScenarioId();
        Scenario scenario = scenarioId != null ? scenarioService.getByIdOrNull(scenarioId, Scenario.class, SecuredBasic_.security, securityContext) : null;
        if (scenario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Scenario with id " + scenarioId);
        }
        clearLogRequest.setScenario(scenario);
    }


    public void clearLog(ClearLogRequest creationContainer, SecurityContextBase securityContext) {
        LogHolder.clearLogger(creationContainer.getScenario().getId(),creationContainer.getScenario().getLogFileResource().getFullPath());
    }

    public FixMissingLogsResponse fixMissingLogFiles(SecurityContextBase securityContext) {
        List<ScenarioTrigger> scenarioTriggers = scenarioTriggerService.listAllScenarioTriggers(new ScenarioTriggerFilter().setMissingLogFile(true), null);
        List<Object> toMerge=new ArrayList<>();
        for (ScenarioTrigger trigger : scenarioTriggers) {
            if(trigger.getLogFileResource()==null&&!trigger.isSoftDelete()){
                FileResource logFileResource = logFileCreatorService.createLogFileNoMerge(trigger.getSecurity(),  LogFileCreatorListener.LOG_TRIGGER, trigger.getName());
                trigger.setLogFileResource(logFileResource);
                toMerge.addAll(List.of(trigger,logFileResource));
            }
        }
        List<Scenario> scenarios = scenarioService.listAllScenarios(new ScenarioFilter().setMissingLogFile(true), null);
        for (Scenario scenario : scenarios) {
            if(scenario.getLogFileResource()==null&&!scenario.isSoftDelete()){
                FileResource logFileResource = logFileCreatorService.createLogFileNoMerge(scenario.getSecurity(),  LogFileCreatorListener.LOG_SCENARIO, scenario.getName());
                scenario.setLogFileResource(logFileResource);
                toMerge.addAll(List.of(scenario,logFileResource));
            }
        }
        scenarioTriggerService.massMerge(toMerge);
        return new FixMissingLogsResponse(scenarios.size(),scenarioTriggers.size());
    }
}
