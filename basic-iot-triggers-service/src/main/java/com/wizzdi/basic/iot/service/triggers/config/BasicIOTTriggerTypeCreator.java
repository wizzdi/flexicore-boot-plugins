package com.wizzdi.basic.iot.service.triggers.config;

import com.flexicore.rules.events.ScenarioEventBase;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.flexicore.rules.request.ScenarioTriggerTypeCreate;
import com.flexicore.rules.request.ScenarioTriggerTypeFilter;
import com.flexicore.rules.service.ScenarioTriggerTypeService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.service.triggers.events.RemoteCreatedTrigger;
import com.wizzdi.basic.iot.service.triggers.events.RemoteStatusChangedTrigger;
import com.wizzdi.basic.iot.service.triggers.events.RemoteUpdatedTrigger;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Configuration
@Extension
public class BasicIOTTriggerTypeCreator implements Plugin {
    private static final List<TriggerTypeHolder> triggerTypes = List.of(
            new TriggerTypeHolder(RemoteCreatedTrigger.class,"fired when remote is created"),
            new TriggerTypeHolder(RemoteStatusChangedTrigger.class,"fired when remote status is changed"),
            new TriggerTypeHolder(RemoteUpdatedTrigger.class,"fired when remote state or any other direct property is changed")
    );




    @Bean
    public BasicIOTTriggerTypeHolder basicIOTTriggerTypeHolder(ScenarioTriggerTypeService scenarioTriggerTypeService, SecurityContext adminSecurityContext){
        Set<String> typeNames = triggerTypes.stream().map(f -> f.type().getCanonicalName()).collect(Collectors.toSet());
        Map<String,ScenarioTriggerType> existing=scenarioTriggerTypeService.listAllScenarioTriggerTypes(new ScenarioTriggerTypeFilter().setEventCanonicalName(typeNames),null).stream().collect(java.util.stream.Collectors.toMap(f->f.getEventCanonicalName(), f->f));
        for (TriggerTypeHolder triggerHolder : triggerTypes) {
            ScenarioTriggerTypeCreate scenarioTriggerTypeCreate=new ScenarioTriggerTypeCreate()
                    .setEventCanonicalName(triggerHolder.type().getCanonicalName())
                    .setName(triggerHolder.description());
            ScenarioTriggerType scenarioTriggerType = Optional.ofNullable(existing.get(triggerHolder.type().getCanonicalName()))
                    .map(f -> scenarioTriggerTypeService.updateScenarioTriggerType(scenarioTriggerTypeCreate, f))
                    .orElseGet(() -> scenarioTriggerTypeService.createScenarioTriggerType(scenarioTriggerTypeCreate, adminSecurityContext));
            existing.put(scenarioTriggerType.getEventCanonicalName(),scenarioTriggerType);

        }
        return new BasicIOTTriggerTypeHolder(new ArrayList<>(existing.values()));
    }

    record TriggerTypeHolder(Class<? extends ScenarioEventBase> type,String description){}

}
