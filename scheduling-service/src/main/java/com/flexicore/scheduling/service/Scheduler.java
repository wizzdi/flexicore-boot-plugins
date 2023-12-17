package com.flexicore.scheduling.service;

import com.flexicore.scheduling.model.*;
import com.flexicore.scheduling.request.ScheduleTimeslotFilter;
import com.flexicore.scheduling.request.ScheduleToActionFilter;
import com.flexicore.scheduling.request.ScheduleUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.ExecuteDynamicExecution;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicExecutionService;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.calendar.astro.SolarTime;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Extension
public class Scheduler implements Plugin, InitializingBean {
//    @Value("${flexicore.scheduling.fetch.intervalMs:#{5 * 60 * 1000}}")
//    private long fetchInterval;
//    @Value("${flexicore.scheduling.initialDelayMs:#{1 * 60 * 1000}}")
//    private long initialDelay;
//    @Value("${flexicore.scheduling.check.intervalMs:#{30 * 1000}}")
    @Value("${flexicore.scheduling.fetch.intervalMs:#{1 * 20 * 1000}}")
    private long fetchInterval;
    @Value("${flexicore.scheduling.initialDelayMs:#{1 * 1 * 1000}}")
    private long initialDelay;
    @Value("${flexicore.scheduling.check.intervalMs:#{10 * 1000}}")
    private long checkInterval;

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
    @Autowired
    private ScheduleService schedulingService;
    @Autowired
    private ScheduleToActionService scheduleToActionService;
    @Autowired
    private ScheduleActionService scheduleActionService;
    @Autowired
    private ScheduleTimeslotService scheduleTimeslotService;
    @Autowired
    @Lazy
    private SecurityContextProvider securityContextProvider;
    @Autowired
    @Qualifier("FCSchedulingExecutor")
    private ExecutorService fCSchedulingExecutor;
    @Autowired
    private DynamicExecutionService dynamicExecutionService;
    private OffsetDateTime lastFetch = null;
    private boolean stop;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    Map<String, Boolean> currentlyRunningSchedules = new ConcurrentHashMap<>();

    public void runSchedules() {
        Map<String, List<ScheduleToAction>> actionsMap = new HashMap<>();
        Map<String, List<ScheduleTimeslot>> timeSlotsMap = new HashMap<>();
        logger.info("Scheduler Waiting For Initial Delay");
        if(initialDelay > 0 ){
            try {
                Thread.sleep(initialDelay);
            } catch (InterruptedException e) {
                logger.error("failed sleeping",e);
            }
        }
        logger.info("Scheduler Started");
        while (!stop) {
            try {
                OffsetDateTime now = OffsetDateTime.now();
                if (lastFetch == null
                        || now.isAfter(lastFetch.plus(fetchInterval,
                        ChronoUnit.MILLIS))) {
                    try {
                        logger.debug("Fetching Schedule to Actions");
                        List<ScheduleToAction> allScheduleToActionLinks = scheduleToActionService.listAllScheduleToActions(new ScheduleToActionFilter(), null).parallelStream().filter(f -> f.getSchedule() != null && !f.getSchedule().isSoftDelete()).collect(Collectors.toList());
                        logger.debug("Fetched {} Actions, names of schedules {} ", allScheduleToActionLinks.size(),allScheduleToActionLinks.stream().map(f->f.getSchedule().getName()).collect(Collectors.joining(",")));
                        List<Schedule> schedules = new ArrayList<>(allScheduleToActionLinks.stream().collect(Collectors.toMap(f -> f.getSchedule().getId(), f -> f.getSchedule(), (a, b) -> a)).values());
                        schedules.stream().map(f->f.getName()).forEach(e->{
                            logger.debug("schedule name {} found in actions",e);
                                });
                        actionsMap = allScheduleToActionLinks.stream().collect(Collectors.groupingBy(f -> f.getSchedule().getId(), Collectors.toList()));
                        for (String id:actionsMap.keySet()) {
                            logger.debug("schedule {} has {} actions", id, actionsMap.get(id).size());
                            String allActions = actionsMap.get(id).stream().map(ScheduleToAction::getScheduleAction)
                                    .map(ScheduleAction::getName).collect(Collectors.joining(","));
                            logger.debug("schedule {} has actions {}", id, allActions);
                        }
                          timeSlotsMap = scheduleTimeslotService.listAllScheduleTimeslots(new ScheduleTimeslotFilter().setSchedule(schedules), null).parallelStream().collect(Collectors.groupingBy(f -> f.getSchedule().getId()));
                        lastFetch = now;
                        logger.debug("Fetched Actions");
                    }
                    catch (Throwable e){
                        logger.error("failed fetching actions",e);
                    }

                }

                try {

                    if (actionsMap.isEmpty()) {
                        logger.info("No Actions found to execute");
                    }else {
                        logger.info("Found {} Actions to execute", actionsMap.size());
                    }
                    List<Map.Entry<String, List<ScheduleToAction>>> filteredActions = actionsMap.entrySet()
                            .parallelStream().filter(f -> shouldRun(f.getValue().get(0).getSchedule(), now)).collect(Collectors.toList());
                    if (filteredActions.isEmpty() && !actionsMap.isEmpty()) {
                        logger.debug("No Filtered Actions");

                    }else {
                        logger.debug("Found {} Filtered that should run before time slot check Actions", filteredActions.size());
                    }
                    //loop through schedules, the key is schedule id.
                    for (Map.Entry<String, List<ScheduleToAction>> entry : filteredActions) {
                        Schedule schedule = entry.getValue().get(0).getSchedule();
                        if (currentlyRunningSchedules.getOrDefault(schedule.getId(), false)) {
                            logger.debug("schedule {} is already running ",  schedule.getName());
                            continue;
                        }
                        logger.debug("Starting to check schedule " + schedule.getName() + "(" + schedule.getId() + ")");
                        SecurityContextBase scheduleSecurityContext = securityContextProvider.getSecurityContext(schedule.getSecurity().getCreator());
                        List<ScheduleTimeslot> timeslots = timeSlotsMap.get(schedule.getId());
                        if (timeslots == null) {
                            logger.debug("no timeslots for schedule " + schedule.getName() + "(" + schedule.getId() + ")");
                            continue;
                        }else {
                            logger.debug("found {}  timeslots for schedule {} , id {} ", timeslots.size(), schedule.getName(), schedule.getId());
                        }
                        List<ScheduleTimeslot> timelostInAffect = timeslots.parallelStream().filter(f -> shouldRun(f, now)).toList();
                        if (timelostInAffect.isEmpty()) {
                            logger.debug("no effective timeslots for schedule {}", schedule.getName());
                            continue;
                        }else {
                            logger.debug("found effective {} timeslots for schedule {} ",timelostInAffect.size(),schedule.getName());
                        }
                        for (ScheduleTimeslot scheduleTimeslot : timelostInAffect) {
                            logger.debug("schedule timeslot " + scheduleTimeslot.getName());
                            try {
                                scheduleTimeslot.setLastExecution(OffsetDateTime.now());
                                schedulingService.merge(scheduleTimeslot);
                                fCSchedulingExecutor.execute(() -> {
                                    try {
                                        currentlyRunningSchedules.put(scheduleTimeslot.getId(), true);
                                        logger.info("executing schedule time " + schedule.getName() + "at timeslot- " + scheduleTimeslot
                                                .getName() + " as user " + scheduleSecurityContext.getUser().getName() + "(" + scheduleSecurityContext.getUser().getId() + ")" + " has arrived");
                                        runSchedule(entry.getValue(), scheduleSecurityContext);
                                        logger.info("done running schedule " + schedule.getName() + " at timeslot " + scheduleTimeslot
                                                .getName());
                                    } catch (Throwable e) {
                                        logger.error("failed executing ", e);
                                    } finally {
                                        currentlyRunningSchedules.put(scheduleTimeslot.getId(), false);

                                    }

                                });

                            } catch (Exception e) {
                                logger.error("failed running schedule " + schedule.getName(), e);
                            }
                        }

                    }
                } catch (Exception e) {
                    logger.error("exception while running schedule", e);
                }

                Thread.sleep(checkInterval);

            } catch (InterruptedException e) {
                logger.warn("interrupted while waiting for schedule check", e);
                stop = true;
            }
        }
        logger.info("Scheduler Stopped");

    }

    private boolean shouldRun(Schedule schedule, OffsetDateTime now) {
        boolean enabled = schedule.isEnabled();
        boolean inTimeFrame = isInTimeFrame(schedule, now);
        boolean day = isDay(schedule, now);
        boolean shouldRun = enabled && inTimeFrame && day;
        String log;
        logger.debug(" ******************** Schedule {},in time frame {} ,day {} ,enabled {} ,should run {} ********************* ",schedule.getName(),inTimeFrame,day,enabled,shouldRun);
        if (!shouldRun) {
            log="Should not run on : " + now + " , enabled: " + enabled + " , in time frame: " + inTimeFrame + " , day: " + day;
            logger.debug("schedule " + schedule.getName() + "(" + schedule.getId() + ") should not run , in timespan: " + inTimeFrame + " , day: " + day + ", enabled:" + enabled);
        }else {
            log="Should run on now : " + now;
        }
        schedulingService.updateSchedule(new ScheduleUpdate().setSchedule(schedule).setLog(log),null);
        return shouldRun;
    }

    private boolean shouldRun(ScheduleTimeslot scheduleTimeslot, OffsetDateTime now) {
        logger.info("Checking schedule timeslot " + scheduleTimeslot.getName() + "(" + scheduleTimeslot.getId() + ")");
        boolean time = isTime(scheduleTimeslot, now);
        boolean previousRun = checkPreviousRun(scheduleTimeslot, now);
        boolean shouldRun = time && previousRun;
        logger.debug("-------------- Checking time slot name {}, is it in time?  {} ,previous run should run ? {}  " , scheduleTimeslot.getName() , time , previousRun);
        logger.info("schedule timeslot " + scheduleTimeslot.getName() + "(" + scheduleTimeslot.getId() + ") should run: " + shouldRun + " , in timespan: " + time + " , previous run: " + previousRun);
        if (!shouldRun) {
            scheduleTimeslot.setLog("Time slot is NOT  effective now: " + now + " , in time frame: " + time + " , previous run: " + previousRun);
            logger.info("schedule timeslot " + scheduleTimeslot.getName() + "(" + scheduleTimeslot.getId() + ") should not run , in timespan: " + time + " , previous run: " + previousRun);

        }else {
            logger.info("schedule timeslot " + scheduleTimeslot.getName() + "(" + scheduleTimeslot.getId() + ") should run , in timespan: " + time + " , previous run: " + previousRun);
            scheduleTimeslot.setLog("Time slot is effective now: " + now + " , in time frame: " + time + " , previous run: " + previousRun);
        }
        if (shouldRun) {

            schedulingService.merge(scheduleTimeslot);
        }
        return shouldRun;

    }

    private boolean checkPreviousRun(ScheduleTimeslot schedule, OffsetDateTime now) {
        if (schedule.getCoolDownIntervalBeforeRepeat() > 0) {

            return schedule.getLastExecution() == null || schedule.getLastExecution().plus(schedule.getCoolDownIntervalBeforeRepeat(), ChronoUnit.MILLIS).isBefore(now);
        } else {
            return schedule.getLastExecution() == null || !schedule.getLastExecution().getDayOfWeek().equals(now.getDayOfWeek());
        }

    }

    private boolean isTime(ScheduleTimeslot timeslot, OffsetDateTime now) {
        Schedule schedule=timeslot.getSchedule();
        if (timeslot.getId().equals("b4677ceb-f6b0-40e4-8335-341e348d87b5")) {
            int a=3;
        }
        if ((timeslot.getStartTime() == null && timeslot.getStartTimeOfTheDayName() == null) || (timeslot.getEndTime() == null && timeslot.getEndTimeOfTheDayName() == null)) {
            return false;
        }

        OffsetTime nowTime = now.toOffsetTime();

        OffsetTime start = timeslot.getStartTime() != null ? getOffSetTime(timeslot.getStartTime(),schedule.getSelectedTimeZone()) : null;
        if (timeslot.getStartTimeOfTheDayName() != null) {
            start = getTimeFromName(timeslot.getStartTimeOfTheDayName(), timeslot.getTimeOfTheDayNameStartLon(), timeslot.getTimeOfTheDayNameStartLat()).orElse(null);
        }
        start = start != null ? start.plus(timeslot.getStartMillisOffset(),
                ChronoUnit.MILLIS) : null;

        OffsetTime end = timeslot.getEndTime() != null ? getOffSetTime(timeslot.getEndTime(),schedule.getSelectedTimeZone()) : null;
        if (timeslot.getEndTimeOfTheDayName() != null) {
            end = getTimeFromName(timeslot.getEndTimeOfTheDayName(), timeslot.getTimeOfTheDayNameEndLon(), timeslot.getTimeOfTheDayNameEndLat()).orElse(null);

        }
        end = end != null ? end.plus(timeslot.getEndMillisOffset(), ChronoUnit.MILLIS) : null;

        return start != null && nowTime.isAfter(start) && (end == null || nowTime.isBefore(end));
    }
    public static OffsetTime getOffSetTime(OffsetDateTime startTime, String timezoneId) {

        ZonedDateTime zonedStartTime = startTime.atZoneSameInstant(startTime.getOffset());


        ZoneId zoneId = ZoneId.of(timezoneId);
        LocalDate currentDate = LocalDate.now(zoneId);


        ZonedDateTime StartZonedDateTime = ZonedDateTime.of(currentDate, zonedStartTime.toLocalTime(), zoneId);


        return StartZonedDateTime.toOffsetDateTime().toOffsetTime();
    }
    private boolean isInTimeFrame(Schedule schedule, OffsetDateTime now) {
        return now.isAfter(schedule.getTimeFrameStart().atZoneSameInstant(ZoneId.of(schedule.getStartTimeOffsetId())).toOffsetDateTime())
                && now.isBefore(schedule.getTimeFrameEnd().atZoneSameInstant(ZoneId.of(schedule.getEndTimeOffsetId())).toOffsetDateTime());
    }

    private boolean isDay(Schedule schedule, OffsetDateTime now) {
        return (schedule.isSunday() && DayOfWeek.SUNDAY.equals(now
                .getDayOfWeek()))
                || (schedule.isMonday() && DayOfWeek.MONDAY.equals(now
                .getDayOfWeek()))
                || (schedule.isTuesday() && DayOfWeek.TUESDAY.equals(now
                .getDayOfWeek()))
                || (schedule.isWednesday() && DayOfWeek.WEDNESDAY.equals(now
                .getDayOfWeek()))
                || (schedule.isThursday() && DayOfWeek.THURSDAY.equals(now
                .getDayOfWeek()))
                || (schedule.isFriday() && DayOfWeek.FRIDAY.equals(now
                .getDayOfWeek()))
                || (schedule.isSaturday() && DayOfWeek.SATURDAY.equals(now
                .getDayOfWeek()));
    }


    public Optional<OffsetTime> getTimeFromName(
            TimeOfTheDayName timeOfTheDayName, double lon, double lat) {
        SolarTime solarTime;
        Optional<Moment> result = Optional.empty();
        switch (timeOfTheDayName) {
            case SUNRISE:
                solarTime = SolarTime.ofLocation(lat, lon);
                result = PlainDate.nowInSystemTime().get(solarTime.sunrise());
                break;
            case SUNSET:
                solarTime = SolarTime.ofLocation(lat, lon);
                result = PlainDate.nowInSystemTime().get(solarTime.sunset());
                break;
        }

        return result.map(f -> f.toLocalTimestamp().toTemporalAccessor()
                .atZone(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime());

    }

    @Transactional
    public void runSchedule(List<ScheduleToAction> schedule,
                            SecurityContextBase securityContext) {
        logger.debug("Starting to run scheduleToActions of size : {}", schedule.size());
        for (ScheduleToAction link : schedule) {
            logger.debug("Will run scheduleToAction {} " , link.getScheduleAction().getName() );
            long start = System.currentTimeMillis();
            boolean failed = false;
            Object response = null;
            ScheduleAction scheduleAction = link.getScheduleAction();
            try {
                DynamicExecution dynamicExecution = scheduleAction
                        .getDynamicExecution();
                if (dynamicExecution != null) {
                    response = dynamicExecutionService.executeDynamicExecution(new ExecuteDynamicExecution().setDynamicExecution(dynamicExecution), securityContext);
                    logger.info("-------------Have executed schedule action"
                            + scheduleAction.getName() + "("
                            + scheduleAction.getId() + ") with response "
                            + response);
                    link.setLastExecution(OffsetDateTime.now());
                    scheduleAction.setLastExecution(OffsetDateTime.now());
                    scheduleActionService.merge(scheduleAction);
                    scheduleToActionService.merge(link);
                } else {
                    logger.warn("Schedule Action "
                            + scheduleAction.getName() + "("
                            + scheduleAction.getId()
                            + ") had null dynamic Execution");

                }
            } catch (Exception e) {
                response = e;
                failed = true;

            } finally {
               // auditSchedule(securityContext, scheduleAction, start, failed, response);
            }

        }

    }


    private boolean matchParameters(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return method.getParameterCount() == 2
                && ScheduleAction.class.isAssignableFrom(parameterTypes[0])
                && SecurityContextBase.class.isAssignableFrom(parameterTypes[1]);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this::runSchedules).start();
    }
}
