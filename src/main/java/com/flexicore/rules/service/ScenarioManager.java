package com.flexicore.rules.service;


import com.flexicore.rules.events.ScenarioEvent;
import com.flexicore.rules.model.*;
import com.flexicore.rules.request.*;
import com.flexicore.rules.response.EvaluateScenarioResponse;
import com.flexicore.rules.response.EvaluateTriggerResponse;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.ExecuteInvokerRequest;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicExecutionService;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicInvokerService;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import org.apache.commons.io.FileUtils;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.script.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.flexicore.rules.service.LogHolder.flush;
import static com.flexicore.rules.service.LogHolder.getLogger;


@Extension
@Component
public class ScenarioManager implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioManager.class);

    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");


    @Autowired
    private ScenarioToTriggerService scenarioToTriggerService;


    @Autowired
    private ScenarioToDataSourceService scenarioToDataSourceService;


    @Autowired
    private ScenarioToActionService scenarioToActionService;

    @Autowired

    private ScenarioTriggerService scenarioTriggerService;


    @Autowired
    private DynamicExecutionService dynamicExecutionService;


    @Autowired
    private DynamicInvokerService dynamicInvokerService;

    @Autowired

    private ScenarioSavableEventService scenarioEventService;


    private boolean isActive(ScenarioTrigger trigger) {
        return trigger.getActiveTill() != null && trigger.getActiveTill().isAfter(OffsetDateTime.now());
    }

    @Async
    @EventListener
    public void handleTrigger(ScenarioEvent scenarioEvent) {
        logger.info("Scenario Trigger Event " + scenarioEvent + "captured by Scenario Manager");
        SecurityContextBase securityContext = scenarioEvent.getSecurityContext();
        List<ScenarioTrigger> triggers = scenarioTriggerService.listAllScenarioTriggers(new ScenarioTriggerFilter().setEventCanonicalNames(Collections.singleton(scenarioEvent.getClass().getCanonicalName())), securityContext);
        List<ScenarioTrigger> activeTriggers = new ArrayList<>();
        List<Object> toMerge = new ArrayList<>();
        for (ScenarioTrigger trigger : triggers) {
            if (!isValid(trigger)) {
                logger.debug("Trigger " + trigger.getName() + "(" + trigger.getId() + ") invalid");
                continue;
            }
            EvaluateTriggerResponse evaluateTriggerResponse = evaluateTrigger(new EvaluateTriggerRequest().setScenarioTrigger(trigger).setScenarioEvent(scenarioEvent));
            if (evaluateTriggerResponse.isActive()) {
                activeTriggers.add(trigger);
            }
            if (changeTriggerState(trigger, scenarioEvent, evaluateTriggerResponse)) {
                logger.info("Trigger " + trigger.getName() + "(" + trigger.getId() + ") state changed to Active till" + trigger.getActiveTill());
                toMerge.add(trigger);
            } else {
                logger.debug("Trigger " + trigger.getName() + "(" + trigger.getId() + ") state stayed Active till" + trigger.getActiveTill());
            }


        }
        scenarioTriggerService.massMerge(toMerge);

        if (activeTriggers.isEmpty()) {
            logger.debug("No Active triggers for event " + scenarioEvent);
            return;
        }

        ScenarioToTriggerFilter scenarioToTriggerFilter = new ScenarioToTriggerFilter()
                .setEnabled(true)
                .setFiring(true)
                .setNonDeletedScenarios(true)
                .setScenarioTrigger(activeTriggers);
        List<ScenarioToTrigger> scenarioToTriggers = activeTriggers.isEmpty() ? new ArrayList<>() : scenarioToTriggerService.listAllScenarioToTriggers(scenarioToTriggerFilter, securityContext);
        Map<String, Scenario> scenarioMap = scenarioToTriggers.stream().collect(Collectors.toMap(f -> f.getScenario().getId(), f -> f.getScenario(), (a, b) -> a));
        Map<String, List<ScenarioToTrigger>> fireingScenarios = scenarioToTriggers.stream().collect(Collectors.groupingBy(f -> f.getScenario().getId()));
        ScenarioToDataSourceFilter scenarioToDataSourceFilter = new ScenarioToDataSourceFilter()
                .setEnabled(true)
                .setScenario(new ArrayList<>(scenarioMap.values()));
        Map<String, List<ScenarioToDataSource>> scenarioToDataSource = scenarioMap.isEmpty() ? new HashMap<>() : scenarioToDataSourceService.listAllScenarioToDataSources(scenarioToDataSourceFilter, securityContext).stream().collect(Collectors.groupingBy(f -> f.getScenario().getId()));
        List<EvaluateScenarioResponse> evaluateScenarioResponses = new ArrayList<>();
        List<ScenarioToAction> scenarioToActions = scenarioMap.isEmpty() ? new ArrayList<>() : scenarioToActionService.listAllScenarioToActions(new ScenarioToActionFilter().setEnabled(true).setScenario(new ArrayList<>(scenarioMap.values())), securityContext);
        Map<String, List<ScenarioToAction>> actions = scenarioToActions.stream().collect(Collectors.groupingBy(f -> f.getScenario().getId()));

        for (Map.Entry<String, List<ScenarioToTrigger>> entry : fireingScenarios.entrySet()) {
            String scenarioId = entry.getKey();
            Scenario scenario = scenarioMap.get(scenarioId);
            List<ScenarioTrigger> scenarioToTriggerList = entry.getValue().stream().sorted(Comparator.comparing(f -> f.getOrdinal())).map(f -> f.getScenarioTrigger()).collect(Collectors.toList());
            List<DataSource> scenarioToDataSources = scenarioToDataSource.getOrDefault(scenarioId, new ArrayList<>()).stream().sorted(Comparator.comparing(f -> f.getOrdinal())).map(f -> f.getDataSource()).collect(Collectors.toList());
            Map<String, ExecuteInvokerRequest> scenarioActions = actions.getOrDefault(scenarioId, new ArrayList<>()).stream().map(f -> f.getScenarioAction()).collect(Collectors.toMap(f -> f.getId(), f -> dynamicExecutionService.getExecuteInvokerRequest(f.getDynamicExecution(), securityContext), (a, b) -> a));

            EvaluateScenarioRequest evaluateScenarioRequest = new EvaluateScenarioRequest()
                    .setScenario(scenario)
                    .setScenarioEvent(scenarioEvent)
                    .setScenarioTriggers(scenarioToTriggerList)
                    .setDataSources(scenarioToDataSources)
                    .setActions(scenarioActions);
            EvaluateScenarioResponse evaluateScenarioResponse = evaluateScenario(evaluateScenarioRequest);
            evaluateScenarioResponses.add(evaluateScenarioResponse);

        }
        for (EvaluateScenarioResponse evaluateScenarioResponse : evaluateScenarioResponses) {
            if (evaluateScenarioResponse.getActions() != null) {
                for (ExecuteInvokerRequest executeInvokerRequest : evaluateScenarioResponse.getActions().values()) {
                    dynamicInvokerService.executeInvoker(executeInvokerRequest, securityContext);
                }
            }
        }


    }

    private boolean isValid(ScenarioTrigger trigger) {
        OffsetDateTime now = OffsetDateTime.now();
        return triggerValidTimes(now, trigger) && scenarioCoolDown(now, trigger);
    }

    private boolean scenarioCoolDown(OffsetDateTime now, ScenarioTrigger trigger) {
        OffsetDateTime cooldownMin = now.plus(trigger.getCooldownIntervalMs(), ChronoUnit.MILLIS);
        boolean cooldown = trigger.getLastActivated() == null || cooldownMin.isAfter(trigger.getLastActivated());
        if (!cooldown) {
            logger.debug("Trigger " + trigger.getName() + "(" + trigger.getId() + ") invalid cooldown (" + cooldownMin + " vs " + now + ")");

        }
        return cooldown;
    }

    private boolean triggerValidTimes(OffsetDateTime now, ScenarioTrigger trigger) {
        OffsetDateTime start = trigger.getValidFrom().withDayOfYear(now.getDayOfYear()).withYear(now.getYear());
        OffsetDateTime end = trigger.getActiveTill().withDayOfYear(now.getDayOfYear()).withYear(now.getYear());
        boolean validTimes = (now.isAfter(start) || now.equals(start)) && (now.isBefore(end) || now.equals(end));
        if (!validTimes) {
            logger.debug("Trigger " + trigger.getName() + "(" + trigger.getId() + ") invalid times (" + start + "-" + end + " vs " + now + ")");
        }
        return validTimes;
    }


    private EvaluateScenarioResponse evaluateScenario(EvaluateScenarioRequest evaluateScenarioRequest) {
        EvaluateScenarioResponse evaluateScenarioResponse = new EvaluateScenarioResponse()
                .setEvaluateScenarioRequest(evaluateScenarioRequest);
        List<ScenarioTrigger> scenarioTriggers = evaluateScenarioRequest.getScenarioTriggers();
        List<DataSource> scenarioToDataSources = evaluateScenarioRequest.getDataSources();
        ScenarioEvent scenarioEvent = evaluateScenarioRequest.getScenarioEvent();
        Scenario scenario = evaluateScenarioRequest.getScenario();
        FileResource script = scenario.getEvaluatingJSCode();
        java.util.logging.Logger scenarioTriggerLogger = getLogger(scenario.getId(), scenario.getLogFileResource().getFullPath());
        try {
            File file = new File(script.getFullPath());
            Bindings bindings = loadScript(file);
            Map<String, ExecuteInvokerRequest> actions = evaluateScenarioRequest.getActions();
            EvaluateScenarioScriptContext scenarioEventScriptContext = new EvaluateScenarioScriptContext(this::fetchEvent)
                    .setScenario(evaluateScenarioRequest.getScenario())
                    .setActions(actions)
                    .setLogger(scenarioTriggerLogger)
                    .setScenarioEvent(scenarioEvent)
                    .setScenarioToDataSources(scenarioToDataSources)
                    .setScenarioTriggers(scenarioTriggers);
            bindings.put("x",scenarioEventScriptContext);
            String functionName = FunctionTypes.EVALUATE.getFunctionName();

            String[] res = (String[]) engine.eval(functionName+"(x);");
            Set<String> idsToRun = Stream.of(res).collect(Collectors.toSet());
            Map<String, ExecuteInvokerRequest> filtered = actions.entrySet().parallelStream().filter(f -> idsToRun.contains(f.getKey())).collect(Collectors.toMap(f -> f.getKey(), f -> f.getValue()));
            evaluateScenarioResponse.setActions(filtered);

        } catch (Exception e) {
            logger.error("failed executing script", e);
            scenarioTriggerLogger.log(Level.SEVERE,
                    "failed executing script: " + e, e);
        } finally {
            flush(scenarioTriggerLogger);
        }

        return evaluateScenarioResponse;
    }

    private ScenarioSavableEvent fetchEvent(String eventId) {
        if (eventId == null) {
            return null;
        }
        List<ScenarioSavableEvent> scenarioEvents = scenarioEventService.listAllScenarioSavableEvents(new ScenarioSavableEventFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setOnlyIds(Collections.singleton(eventId))),null);
        return scenarioEvents.isEmpty() ? null : scenarioEvents.get(0);
    }

    private boolean changeTriggerState(ScenarioTrigger trigger, ScenarioEvent scenarioEvent, EvaluateTriggerResponse evaluateTriggerResponse) {
        boolean update = false;
        boolean active = isActive(trigger);
        if (active != evaluateTriggerResponse.isActive()) {
            OffsetDateTime activeTill;
            if (active) {
                activeTill = trigger.getActiveMs() > 0 ? OffsetDateTime.now().plus(trigger.getActiveMs(), ChronoUnit.MILLIS) : OffsetDateTime.MAX;
                trigger.setLastActivated(OffsetDateTime.now());
            } else {
                activeTill = OffsetDateTime.now();
            }
            trigger.setActiveTill(activeTill);


            update = true;
        }
        if (trigger.getLastEventId() == null || (!trigger.getLastEventId().equals(scenarioEvent.getId()))) {
            trigger.setLastEventId(scenarioEvent.getId());
        }
        return update;

    }

    private EvaluateTriggerResponse evaluateTrigger(EvaluateTriggerRequest evaluateTriggerRequest) {
        EvaluateTriggerResponse evaluateTriggerResponse = new EvaluateTriggerResponse();
        ScenarioTrigger scenarioTrigger = evaluateTriggerRequest.getScenarioTrigger();
        ScenarioEvent scenarioEvent = evaluateTriggerRequest.getScenarioEvent();
        FileResource script = scenarioTrigger.getEvaluatingJSCode();
        java.util.logging.Logger scenarioTriggerLogger = getLogger(scenarioTrigger.getId(), scenarioTrigger.getLogFileResource().getFullPath());
        try {
            File file = new File(script.getFullPath());
            Bindings loaded = loadScript(file);
            EvaluateTriggerScriptContext scenarioEventScriptContext = new EvaluateTriggerScriptContext()
                    .setLogger(scenarioTriggerLogger)
                    .setScenarioEvent(scenarioEvent);
            loaded.put("x",scenarioEventScriptContext);
            boolean res = (boolean)engine.eval( FunctionTypes.EVALUATE.getFunctionName()+"(x);",loaded);
            evaluateTriggerResponse.setActive(res);

        } catch (Exception e) {
            logger.error("failed executing script", e);
            scenarioTriggerLogger.log(Level.SEVERE,
                    "failed executing script: " + e.toString(), e);
        } finally {
            flush(scenarioTriggerLogger);
        }

        return evaluateTriggerResponse;

    }

    private Bindings loadScript(File file)
            throws IOException, ScriptException {
        String script = FileUtils
                .readFileToString(file, StandardCharsets.UTF_8);
        CompiledScript compiled = ((Compilable) engine).compile(script);
        Bindings bindings=engine.createBindings();
        Object eval = compiled.eval(bindings);
        return bindings;
    }

    private String buildFunctionTableFunction(FunctionTypes... functionTypes) {
        String base = "function () {" + "  return { ";
        String functions = Stream
                .of(functionTypes)
                .map(f -> "'" + f.getFunctionName() + "':"
                        + f.getFunctionName()).collect(Collectors.joining(","));
        base += functions;
        base += "};";
        base += "};";
        return base;

    }
}
