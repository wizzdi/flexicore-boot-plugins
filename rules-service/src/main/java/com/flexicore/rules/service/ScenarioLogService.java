package com.flexicore.rules.service;

import com.flexicore.model.SecuredBasic_;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.request.ClearLogRequest;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class ScenarioLogService implements Plugin {
    @Autowired
    private ScenarioService scenarioService;

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
}
