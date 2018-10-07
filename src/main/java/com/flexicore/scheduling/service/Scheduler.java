package com.flexicore.scheduling.service;

import com.flexicore.scheduling.containers.request.SchedulingFiltering;
import com.flexicore.scheduling.init.Config;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleTimeslot;
import com.flexicore.scheduling.model.ScheduleToAction;
import com.flexicore.security.SecurityContext;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Scheduler implements Runnable {
    private static final long FETCH_INTERVAL = 60 * 1000;
    private static final long CHECK_INTERVAL = 1000;

    private boolean stop;
    private Logger logger;
    private SchedulingService schedulingService;
    private SecurityContext securityContext;
    private LocalDateTime lastFetch = null;
    private ExecutorService executorService= Executors.newFixedThreadPool(Config.maxSchedulingThreads);

    public Scheduler(Logger logger, SchedulingService schedulingService, SecurityContext securityContext) {
        this.logger = logger;
        this.schedulingService = schedulingService;
        this.securityContext = securityContext;
    }

    @Override
    public void run() {
        SchedulingFiltering schedulingFiltering = new SchedulingFiltering();
        Map<String, List<ScheduleToAction>> actionsMap = new HashMap<>();
        Map<String, List<ScheduleTimeslot>> timeSlotsMap = new HashMap<>();
        logger.info("Scheduler Started");
        while (!stop) {
            try {
                LocalDateTime now = LocalDateTime.now();

                if (lastFetch == null || now.isAfter(lastFetch.plus(FETCH_INTERVAL, ChronoUnit.MILLIS))) {
                    actionsMap = schedulingService.getAllScheduleLinks().parallelStream().filter(f -> f.getLeftside() != null).collect(Collectors.groupingBy(f -> f.getLeftside().getId(), Collectors.toList()));
                    timeSlotsMap = schedulingService.getAllTimeSlots(actionsMap.keySet(), null).parallelStream().collect(Collectors.groupingBy(f -> f.getSchedule().getId()));
                    lastFetch = now;

                }
                try {
                    Map<String,Boolean> currentlyRunningSchedules=new ConcurrentHashMap<>();
                    for (Map.Entry<String, List<ScheduleToAction>> entry : actionsMap.entrySet()) {
                        Schedule schedule = entry.getValue().get(0).getLeftside();
                        boolean shouldRunSchedule = shouldRun(schedule, now);
                        if (shouldRunSchedule) {
                            List<ScheduleTimeslot> timeslots = timeSlotsMap.get(schedule.getId());
                            if (timeslots == null) {
                                continue;
                            }
                            for (ScheduleTimeslot scheduleTimeslot : timeslots) {
                                try {
                                    boolean shouldRunTimeslot = shouldRun(scheduleTimeslot, now);
                                    boolean executing=currentlyRunningSchedules.getOrDefault(scheduleTimeslot.getId(),false);
                                    if (shouldRunTimeslot&&!executing) {
                                        scheduleTimeslot.setLastExecution(LocalDateTime.now());
                                        schedulingService.merge(scheduleTimeslot);
                                        executorService.execute(()->{
                                            try {
                                                currentlyRunningSchedules.put(scheduleTimeslot.getId(), true);
                                                logger.info("schedule time " + schedule.getName() + "at timeslot- " + scheduleTimeslot.getName() + " has arrived");
                                                schedulingService.runSchedule(entry.getValue(), securityContext);
                                                logger.info("done running schedule " + schedule.getName() + " at timeslot " + scheduleTimeslot.getName());
                                            }
                                            catch (Exception e){
                                                logger.log(Level.SEVERE,"failed executing ",e);
                                            }
                                            finally {
                                                currentlyRunningSchedules.put(scheduleTimeslot.getId(), false);

                                            }


                                        });

                                    }
                                } catch (Exception e) {
                                    logger.log(Level.SEVERE, "failed running schedule " + schedule.getName(), e);
                                }
                            }

                        }


                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "exception while running schedule", e);
                }

                Thread.sleep(CHECK_INTERVAL);

            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "interrupted while waiting for schedule check", e);
                stop = true;
            }
        }
        logger.info("Scheduler Stopped");

    }

    private boolean shouldRun(Schedule schedule, LocalDateTime now) {
        return schedule.isEnabled() && isInTimeFrame(schedule, now) && isDay(schedule, now);
    }

    private boolean shouldRun(ScheduleTimeslot scheduleTimeslot, LocalDateTime now) {
        return isTime(scheduleTimeslot, now) && checkPreviousRun(scheduleTimeslot, now);

    }

    private boolean checkPreviousRun(ScheduleTimeslot schedule, LocalDateTime now) {
        if (schedule.getCoolDownIntervalBeforeRepeat() > 0) {
            return schedule.getLastExecution() == null || schedule.getLastExecution().plus(schedule.getCoolDownIntervalBeforeRepeat(), ChronoUnit.MILLIS).isBefore(now);
        } else {
            return schedule.getLastExecution() == null || !schedule.getLastExecution().getDayOfWeek().equals(now.getDayOfWeek());
        }

    }

    private boolean isTime(ScheduleTimeslot timeslot, LocalDateTime now) {
        if ((timeslot.getStartTime() == null && timeslot.getStartTimeOfTheDayName()==null)
                || (timeslot.getEndTime() == null&& timeslot.getEndTimeOfTheDayName()==null )) {
            return false;
        }
        LocalTime nowTime = now.toLocalTime();
        LocalTime start = timeslot.getStartTime()!=null?timeslot.getStartTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().toLocalTime():null;
        if (timeslot.getStartTimeOfTheDayName() != null) {
            start = schedulingService.getTimeFromName(timeslot.getStartTimeOfTheDayName(), timeslot.getTimeOfTheDayNameStartLon(), timeslot.getTimeOfTheDayNameStartLat()).orElse(null);
        }
            start=start!=null?start.plus(timeslot.getStartMillisOffset(),ChronoUnit.MILLIS):null;

        LocalTime end = timeslot.getEndTime() != null ? timeslot.getEndTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().toLocalTime() : null;
        if (timeslot.getEndTimeOfTheDayName() != null) {
            end = schedulingService.getTimeFromName(timeslot.getEndTimeOfTheDayName(), timeslot.getTimeOfTheDayNameEndLon(), timeslot.getTimeOfTheDayNameEndLat()).orElse(null);

        }
            end=end!=null?end.plus(timeslot.getEndMillisOffset(),ChronoUnit.MILLIS):null;



        return start != null && nowTime.isAfter(start) && (end == null || nowTime.isBefore(end));
    }

    private boolean isInTimeFrame(Schedule schedule, LocalDateTime now) {
        return now.isAfter(schedule.getTimeFrameStart()) && now.isBefore(schedule.getTimeFrameEnd());
    }

    private boolean isDay(Schedule schedule, LocalDateTime now) {
        return (schedule.isSunday() && DayOfWeek.SUNDAY.equals(now.getDayOfWeek())) ||
                (schedule.isMonday() && DayOfWeek.MONDAY.equals(now.getDayOfWeek())) ||
                (schedule.isTuesday() && DayOfWeek.TUESDAY.equals(now.getDayOfWeek())) ||
                (schedule.isWednesday() && DayOfWeek.WEDNESDAY.equals(now.getDayOfWeek())) ||
                (schedule.isThursday() && DayOfWeek.THURSDAY.equals(now.getDayOfWeek())) ||
                (schedule.isFriday() && DayOfWeek.FRIDAY.equals(now.getDayOfWeek())) ||
                (schedule.isSaturday() && DayOfWeek.SATURDAY.equals(now.getDayOfWeek()));
    }
}
