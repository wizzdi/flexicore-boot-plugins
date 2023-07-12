package com.flexicore.scheduling.service;

import com.flexicore.scheduling.model.*;
import com.flexicore.scheduling.request.ScheduleTimeslotFilter;
import com.flexicore.scheduling.request.ScheduleToActionFilter;
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

@Component
@Extension
public class Scheduler implements Plugin, InitializingBean {
    @Value("${flexicore.scheduling.fetch.intervalMs:#{5 * 60 * 1000}}")
    private long fetchInterval;
    @Value("${flexicore.scheduling.initialDelayMs:#{1 * 60 * 1000}}")
    private long initialDelay;
    @Value("${flexicore.scheduling.check.intervalMs:#{60 * 1000}}")
    private long checkInterval;

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
    @Autowired
    private ScheduleService schedulingService;
    @Autowired
    private ScheduleToActionService scheduleToActionService;
    @Autowired
    private ScheduleTimeslotService scheduleTimeslotService;
    @Autowired
    @Lazy
    private SecurityContextProvider securityContextProvider;
    @Autowired
    @Qualifier("FCSchedulingExecutor")
    private ExecutorService FCSchedulingExecutor;
    @Autowired
    private DynamicExecutionService dynamicExecutionService;
    private OffsetDateTime lastFetch = null;
    private boolean stop;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


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
                        List<ScheduleToAction> allScheduleToActionLinks = scheduleToActionService.listAllScheduleToActions(new ScheduleToActionFilter(), null).parallelStream().filter(f -> f.getSchedule() != null && !f.getSchedule().isSoftDelete()).collect(Collectors.toList());
                        List<Schedule> schedules = new ArrayList<>(allScheduleToActionLinks.stream().collect(Collectors.toMap(f -> f.getSchedule().getId(), f -> f.getSchedule(), (a, b) -> a)).values());
                        actionsMap = allScheduleToActionLinks.stream().collect(Collectors.groupingBy(f -> f.getSchedule().getId(), Collectors.toList()));
                        timeSlotsMap = scheduleTimeslotService.listAllScheduleTimeslots(new ScheduleTimeslotFilter().setSchedule(schedules), null).parallelStream().collect(Collectors.groupingBy(f -> f.getSchedule().getId()));
                        lastFetch = now;
                        logger.debug("Fetched Actions");
                    }
                    catch (Throwable e){
                        logger.error("failed fetching actions",e);
                    }

                }
                try {
                    Map<String, Boolean> currentlyRunningSchedules = new ConcurrentHashMap<>();
                    if (actionsMap.isEmpty()) {
                        logger.debug("No Actions");
                    }
                    List<Map.Entry<String, List<ScheduleToAction>>> filteredActions = actionsMap.entrySet().parallelStream().filter(f -> shouldRun(f.getValue().get(0).getSchedule(), now)).collect(Collectors.toList());
                    if (filteredActions.isEmpty() && !actionsMap.isEmpty()) {
                        logger.debug("No Filtered Actions");

                    }
                    for (Map.Entry<String, List<ScheduleToAction>> entry : filteredActions) {
                        Schedule schedule = entry.getValue().get(0).getSchedule();
                        SecurityContextBase scheduleSecurityContext = securityContextProvider.getSecurityContext(schedule.getSecurity().getCreator());
                        List<ScheduleTimeslot> timeslots = timeSlotsMap.get(schedule.getId());
                        if (timeslots == null) {
                            logger.debug("no timeslots for schedule " + schedule.getName() + "(" + schedule.getId() + ")");
                            continue;
                        }
                        for (ScheduleTimeslot scheduleTimeslot : timeslots.parallelStream().filter(f -> shouldRun(f, now) && !currentlyRunningSchedules.getOrDefault(f.getId(), false)).collect(Collectors.toList())) {
                            try {
                                scheduleTimeslot.setLastExecution(OffsetDateTime.now());
                                schedulingService.merge(scheduleTimeslot);
                                FCSchedulingExecutor.execute(() -> {
                                    try {
                                        currentlyRunningSchedules.put(scheduleTimeslot.getId(), true);
                                        logger.info("schedule time " + schedule.getName() + "at timeslot- " + scheduleTimeslot
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
        if (!shouldRun) {
            logger.debug("schedule " + schedule.getName() + "(" + schedule.getId() + ") should not run , in timespan: " + inTimeFrame + " , day: " + day + ", enabled:" + enabled);
        }
        return shouldRun;
    }

    private boolean shouldRun(ScheduleTimeslot scheduleTimeslot, OffsetDateTime now) {
        boolean time = isTime(scheduleTimeslot, now);
        boolean previousRun = checkPreviousRun(scheduleTimeslot, now);
        boolean shouldRun = time && previousRun;
        if (!shouldRun) {
            logger.debug("schedule timeslot " + scheduleTimeslot.getName() + "(" + scheduleTimeslot.getId() + ") should not run , in timespan: " + time + " , previous run: " + previousRun);

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
        if ((timeslot.getStartTime() == null && timeslot.getStartTimeOfTheDayName() == null) || (timeslot.getEndTime() == null && timeslot.getEndTimeOfTheDayName() == null)) {
            return false;
        }

        OffsetTime nowTime = now.toOffsetTime();

        OffsetTime start = timeslot.getStartTime() != null ? timeslot.getStartTime().withOffsetSameInstant(ZoneOffset.of(timeslot.getStartTimeOffsetId())).toOffsetTime() : null;
        if (timeslot.getStartTimeOfTheDayName() != null) {
            start = getTimeFromName(timeslot.getStartTimeOfTheDayName(), timeslot.getTimeOfTheDayNameStartLon(), timeslot.getTimeOfTheDayNameStartLat()).orElse(null);
        }
        start = start != null ? start.plus(timeslot.getStartMillisOffset(),
                ChronoUnit.MILLIS) : null;

        OffsetTime end = timeslot.getEndTime() != null ? timeslot.getEndTime().withOffsetSameInstant(ZoneOffset.of(timeslot.getEndTimeOffsetId())).toOffsetTime() : null;
        if (timeslot.getEndTimeOfTheDayName() != null) {
            end = getTimeFromName(timeslot.getEndTimeOfTheDayName(), timeslot.getTimeOfTheDayNameEndLon(), timeslot.getTimeOfTheDayNameEndLat()).orElse(null);

        }
        end = end != null ? end.plus(timeslot.getEndMillisOffset(), ChronoUnit.MILLIS) : null;

        return start != null && nowTime.isAfter(start) && (end == null || nowTime.isBefore(end));
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
        for (ScheduleToAction link : schedule) {

            long start = System.currentTimeMillis();
            boolean failed = false;
            Object response = null;
            ScheduleAction scheduleAction = link.getScheduleAction();
            try {
                DynamicExecution dynamicExecution = scheduleAction
                        .getDynamicExecution();
                if (dynamicExecution != null) {
                    response = dynamicExecutionService.executeDynamicExecution(new ExecuteDynamicExecution().setDynamicExecution(dynamicExecution), securityContext);
                } else {
                    logger.warn("Schedule Action "
                            + scheduleAction.getName() + "("
                            + scheduleAction.getId()
                            + ") had null dynamic Exection");
                }
            } catch (Exception e) {
                response = e;
                failed = true;

            } finally {
                //auditSchedule(securityContext, scheduleAction, start, failed, response);
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
