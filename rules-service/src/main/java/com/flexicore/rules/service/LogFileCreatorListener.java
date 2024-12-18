package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioTrigger;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.request.FileResourceCreate;
import com.wizzdi.flexicore.file.service.FileResourceService;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@Extension
public class LogFileCreatorListener implements Plugin {

    public static final String LOG_TRIGGER = "log-trigger-";
    public static final String LOG_SCENARIO = "log-scenario-";
    @Autowired
    private LogFileCreatorService logFileCreatorService;
    @Autowired
    private FileResourceService fileResourceService;


    @EventListener
    public void onScenarioTriggerCreated(BasicCreated<ScenarioTrigger> scenarioTriggerCreated){
        ScenarioTrigger scenarioTrigger = scenarioTriggerCreated.getBaseclass();
        if(scenarioTrigger.getLogFileResource()==null&&!scenarioTrigger.isSoftDelete()){
            FileResource logFileResource = logFileCreatorService.createLogFileNoMerge(scenarioTrigger, LOG_TRIGGER, scenarioTrigger.getName());
            scenarioTrigger.setLogFileResource(logFileResource);
            fileResourceService.massMerge(List.of(scenarioTrigger,logFileResource));
        }
    }



    @EventListener
    public void onScenarioCreated(BasicCreated<Scenario> scenarioCreated){
        Scenario scenario = scenarioCreated.getBaseclass();
        if(scenario.getLogFileResource()==null&&!scenario.isSoftDelete()){
            FileResource logFileResource = logFileCreatorService.createLogFileNoMerge(scenario, LOG_SCENARIO, scenario.getName());
            scenario.setLogFileResource(logFileResource);
            fileResourceService.massMerge(List.of(scenario,logFileResource));
        }
    }


}
