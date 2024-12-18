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
public class LogFileCreatorService implements Plugin {

    @Autowired
    private FileResourceService fileResourceService;
    @Autowired
    private SecurityContextProvider securityContextProvider;


    public FileResource createLogFileNoMerge(Baseclass security, String logPrefix, String name) {
        SecurityContext securityContext = securityContextProvider.getSecurityContext(security.getCreator());
        securityContext.setTenantToCreateIn(security.getTenant());
        File log = new File(fileResourceService.generateNewPathForFileResource(logPrefix + replaceWhiteSpacesWithUnderscore(name), securityContext.getUser()) + ".log");
        FileResourceCreate fileResourceCreate = new FileResourceCreate()
                .setOriginalFilename(log.getName())
                .setActualFilename(log.getName())
                .setFullPath(log.getAbsolutePath())
                .setName(log.getName());
        return fileResourceService.createFileResourceNoMerge(fileResourceCreate, securityContext);
    }


    private static String replaceWhiteSpacesWithUnderscore(String name) {
        return name.replaceAll("\\s+", "_");
    }
}
