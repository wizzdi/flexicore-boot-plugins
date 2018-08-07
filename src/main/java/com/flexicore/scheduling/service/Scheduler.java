package com.flexicore.scheduling.service;

import com.flexicore.scheduling.containers.request.SchedulingFiltering;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleToAction;
import com.flexicore.security.SecurityContext;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Scheduler(Logger logger, SchedulingService schedulingService, SecurityContext securityContext) {
        this.logger = logger;
        this.schedulingService = schedulingService;
        this.securityContext = securityContext;
    }

    @Override
    public void run() {
        SchedulingFiltering schedulingFiltering = new SchedulingFiltering();
        Map<String,List<ScheduleToAction> > list = new HashMap<>();
        logger.info("Scheduler Started");
        while (!stop) {
            try {
                LocalDateTime now = LocalDateTime.now();

                if (lastFetch == null || now.isAfter(lastFetch.plus(FETCH_INTERVAL, ChronoUnit.MILLIS))) {
                    list = schedulingService.getAllScheduleLinks().parallelStream().filter(f->f.getLeftside()!=null).collect(Collectors.groupingBy(f->f.getLeftside().getId(),Collectors.toList()));
                    lastFetch = now;

                }
                try {
                    for (Map.Entry<String, List<ScheduleToAction>> entry : list.entrySet()) {
                        Schedule schedule = entry.getValue().get(0).getLeftside();
                        try {
                            boolean shouldRun = shouldRun(schedule, now);
                            if (shouldRun) {
                                schedule.setLastExecution(LocalDateTime.now());
                                schedulingService.merge(schedule);
                                logger.info("schedule time " + schedule.getName() + " has arrived");
                                schedulingService.runSchedule(entry.getValue(), securityContext);
                                logger.info("done running schedule " + schedule.getName());

                            }
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "failed running schedule " + schedule.getName());
                        }

                    }
                }
                catch (Exception e){
                    logger.log(Level.SEVERE,"exception while running schedule",e);
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
        return isInTimeFrame(schedule,now) && isDay(schedule,now)&&isTime(schedule,now) && checkPreviousRun(schedule,now);
    }

    private boolean checkPreviousRun(Schedule schedule, LocalDateTime now) {
        LocalTime nowTime = now.toLocalTime();
        if(schedule.getCoolDownIntervalBeforeRepeat()> 0){
            return schedule.getLastExecution()==null || schedule.getLastExecution().toLocalTime().plus(schedule.getCoolDownIntervalBeforeRepeat(),ChronoUnit.MILLIS).isAfter(nowTime);
        }
        else{
            return schedule.getLastExecution()==null ||!isTime(schedule,schedule.getLastExecution());
        }

    }

    private boolean isTime(Schedule schedule, LocalDateTime now) {
        LocalTime nowTime = now.toLocalTime();
        LocalTime start=schedule.getTimeFrameStart().toLocalTime();
        if(schedule.getTimeOfTheDayName()!=null){
            start=schedulingService.getTimeFromName(schedule).orElse(null);
        }
        LocalTime end=schedule.getTimeOfTheDayEnd()!=null?schedule.getTimeOfTheDayEnd().toLocalTime():null;


        return start != null && nowTime.isAfter(start) && (end==null || nowTime.isBefore(end));
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
