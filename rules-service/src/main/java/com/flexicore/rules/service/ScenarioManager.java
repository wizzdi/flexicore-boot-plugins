package com.flexicore.rules.service;


import com.flexicore.model.SecurityTenant;
import com.flexicore.model.SecurityUser;
import com.flexicore.rules.events.ScenarioEvent;
import com.flexicore.rules.model.*;
import com.flexicore.rules.request.*;
import com.flexicore.rules.response.EvaluateScenarioResponse;
import com.flexicore.rules.response.EvaluateTriggerResponse;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.ExecuteInvokerRequest;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.ExecuteInvokersResponse;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicExecutionService;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicInvokerService;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    private static final ScriptEngine engine = new ScriptEngineManager(ScenarioManager.class.getClassLoader()).getEngineByName("rhino");


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

    @Autowired
    private SecurityContextProvider securityContextProvider;



    private boolean isActive(ScenarioTrigger trigger) {
        return trigger.getActiveTill() != null && trigger.getActiveTill().isAfter(OffsetDateTime.now());
    }

    @Async
    @EventListener
    public <T extends ScenarioEvent> void handleTrigger(T scenarioEvent) {
        logger.info("Scenario Trigger Event " + scenarioEvent + "captured by Scenario Manager");
        List<SecurityTenant> tenants = scenarioEvent.getSecurityContext().getTenants();
        if(tenants.isEmpty()){
            logger.debug("No tenants for security context ");
            return;
        }
        List<ScenarioTrigger> triggers = scenarioTriggerService.listAllScenarioTriggers(new ScenarioTriggerFilter().setTenants(tenants).setEventCanonicalNames(Collections.singleton(scenarioEvent.getClass().getCanonicalName())), null);
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
                .setNonDeletedScenarios(true)
                .setScenarioTrigger(activeTriggers);
        List<ScenarioToTrigger> scenarioToTriggers = activeTriggers.isEmpty() ? new ArrayList<>() : scenarioToTriggerService.listAllScenarioToTriggers(scenarioToTriggerFilter, null);
        Map<String, Scenario> scenarioMap = scenarioToTriggers.stream().collect(Collectors.toMap(f -> f.getScenario().getId(), f -> f.getScenario(), (a, b) -> a));
        Map<String, SecurityUser> creatorMap = scenarioMap.values().stream().map(f -> f.getSecurity()).map(f -> f.getCreator()).collect(Collectors.toMap(f -> f.getId(), f -> f, (a, b) -> a));
        Map<String,SecurityContextBase> securityContextMap=creatorMap.entrySet().stream().collect(Collectors.toMap(f->f.getKey(),f->securityContextProvider.getSecurityContext(f.getValue())));
        Map<String, List<ScenarioToTrigger>> triggersForScenario = scenarioToTriggers.stream().collect(Collectors.groupingBy(f -> f.getScenario().getId()));
        ScenarioToDataSourceFilter scenarioToDataSourceFilter = new ScenarioToDataSourceFilter()
                .setEnabled(true)
                .setScenario(new ArrayList<>(scenarioMap.values()));
        Map<String, List<ScenarioToDataSource>> scenarioToDataSource = scenarioMap.isEmpty() ? new HashMap<>() : scenarioToDataSourceService.listAllScenarioToDataSources(scenarioToDataSourceFilter, null).stream().collect(Collectors.groupingBy(f -> f.getScenario().getId()));
        List<EvaluateScenarioResponse> evaluateScenarioResponses = new ArrayList<>();
        List<ScenarioToAction> scenarioToActions = scenarioMap.isEmpty() ? new ArrayList<>() : scenarioToActionService.listAllScenarioToActions(new ScenarioToActionFilter().setEnabled(true).setScenario(new ArrayList<>(scenarioMap.values())), null);
        Map<String, ScenarioAction> actionsById = scenarioToActions.stream().map(f -> f.getScenarioAction()).collect(Collectors.toMap(f -> f.getId(), f -> f, (a, b) -> a));
        Map<String, List<ScenarioToAction>> actionsByScenario = scenarioToActions.stream().collect(Collectors.groupingBy(f -> f.getScenario().getId()));

        for (Map.Entry<String, List<ScenarioToTrigger>> entry : triggersForScenario.entrySet()) {
            if (entry.getValue().stream().noneMatch(f -> f.isFiring())) {
                logger.debug("Scenario " + entry.getKey() + " has no firing triggers");
                continue;
            }
            String scenarioId = entry.getKey();
            Scenario scenario = scenarioMap.get(scenarioId);
            SecurityContextBase securityContext = securityContextMap.get(scenario.getSecurity().getCreator().getId()).setTenantToCreateIn(scenario.getSecurity().getTenant());
            List<ScenarioTrigger> scenarioToTriggerList = entry.getValue().stream().sorted(Comparator.comparing(f -> f.getOrdinal())).map(f -> f.getScenarioTrigger()).collect(Collectors.toList());
            List<DataSource> scenarioToDataSources = scenarioToDataSource.getOrDefault(scenarioId, new ArrayList<>()).stream().sorted(Comparator.comparing(f -> f.getOrdinal())).map(f -> f.getDataSource()).collect(Collectors.toList());
            List<ActionContext> scenarioActions = actionsByScenario.getOrDefault(scenarioId, new ArrayList<>()).stream().map(f -> f.getScenarioAction()).collect(Collectors.toMap(f -> f.getId(), f -> dynamicExecutionService.getExecuteInvokerRequest(f.getDynamicExecution(), securityContext), (a, b) -> a)).entrySet().stream().map(f -> new ActionContext(f.getKey(), f.getValue())).toList();

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

                for (Map.Entry<String, ExecuteInvokerRequest> entry : evaluateScenarioResponse.getActions().entrySet()) {

                        String actionId = entry.getKey();
                        ScenarioAction scenarioAction = actionsById.get(actionId);
                        ExecuteInvokerRequest executeInvokerRequest = entry.getValue();
                        Scenario scenario = evaluateScenarioResponse.getEvaluateScenarioRequest().getScenario();
                        SecurityContextBase securityContext = securityContextMap.get(scenario.getSecurity().getCreator().getId()).setTenantToCreateIn(scenario.getSecurity().getTenant());
                    java.util.logging.Logger scenarioTriggerLogger = getLogger(scenario.getId(), scenario.getLogFileResource().getFullPath());

                    String message = "Executing action %s(%s) for scenario %s(%s)".formatted(scenarioAction.getName(), scenarioAction.getId(), scenario.getName(), scenario.getId());
                    scenarioTriggerLogger.info(message);
                    logger.info(message);
                    try {
                        ExecuteInvokersResponse executeInvokersResponse = dynamicInvokerService.executeInvoker(executeInvokerRequest, securityContext);
                        String resultMessage="Executed action %s(%s) for scenario %s(%s) with result %s".formatted(scenarioAction.getName(), scenarioAction.getId(), scenario.getName(), scenario.getId(), executeInvokersResponse);
                        scenarioTriggerLogger.info(resultMessage);

                    }
                    catch (Throwable e){
                        logger.error("failed executing action",e);
                        scenarioTriggerLogger.log(Level.SEVERE,
                                "failed executing action: " + e, e);


                    }
                    finally {
                        flush(scenarioTriggerLogger);
                    }
                }
            }
        }


    }

    private boolean isValid(ScenarioTrigger trigger) {
        ZonedDateTime now = ZonedDateTime.now();
        return triggerValidTimes(now, trigger) && scenarioCoolDown(now, trigger);
    }

    private boolean scenarioCoolDown(ZonedDateTime now, ScenarioTrigger trigger) {
        ZonedDateTime cooldownMin = now.plus(trigger.getCooldownIntervalMs(), ChronoUnit.MILLIS);
        boolean cooldown = trigger.getLastActivated() == null || cooldownMin.isAfter(trigger.getLastActivated().atZoneSameInstant(ZoneId.of(trigger.getTimeZoneId())));
        if (!cooldown) {
            logger.debug("Trigger " + trigger.getName() + "(" + trigger.getId() + ") invalid cooldown (" + cooldownMin + " vs " + now + ")");

        }
        return cooldown;
    }

    private boolean triggerValidTimes(ZonedDateTime now, ScenarioTrigger trigger) {
        ZonedDateTime start = Optional.ofNullable(trigger.getValidFrom())
                .map(f -> f.atZoneSameInstant(ZoneId.of(trigger.getTimeZoneId()))
                        .withDayOfYear(now.getDayOfYear()).withYear(now.getYear())).orElse(OffsetDateTime.MIN.toZonedDateTime());
        ZonedDateTime end = Optional.ofNullable(trigger.getValidTill())
                .map(f -> f.atZoneSameInstant(ZoneId.of(trigger.getTimeZoneId())).withDayOfYear(now.getDayOfYear()).withYear(now.getYear()))
                .orElse(OffsetDateTime.MAX.toZonedDateTime());
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
            List<ActionContext> actions = evaluateScenarioRequest.getActions();
            EvaluateScenarioScriptContext scenarioEventScriptContext = new EvaluateScenarioScriptContext(this::fetchEvent)
                    .setScenario(evaluateScenarioRequest.getScenario())
                    .setActions(actions)
                    .setLogger(scenarioTriggerLogger)
                    .setScenarioEvent(scenarioEvent)
                    .setScenarioToDataSources(scenarioToDataSources)
                    .setScenarioTriggers(scenarioTriggers);
            bindings.put("x",scenarioEventScriptContext);
            String functionName = FunctionTypes.EVALUATE.getFunctionName();

            List<String> res = (List<String>) engine.eval(functionName + "(x);", bindings);
            Set<String> idsToRun = new HashSet<>(res);
            Map<String, ExecuteInvokerRequest> filtered = actions.parallelStream().filter(f -> idsToRun.contains(f.getId())).collect(Collectors.toMap(f -> f.getId(), f -> f.getExecuteInvokerRequest()));
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
                activeTill = trigger.getActiveMs() > 0 ? OffsetDateTime.now().plus(trigger.getActiveMs(), ChronoUnit.MILLIS) : OffsetDateTime.now().plusYears(1000);
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
