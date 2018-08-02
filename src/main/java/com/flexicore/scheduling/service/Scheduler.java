package com.flexicore.scheduling.service;

import com.flexicore.scheduling.containers.request.SchedulingFiltering;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.security.SecurityContext;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        List<Schedule> list = new ArrayList<>();
        while (!stop) {
            try {
                LocalDateTime now = LocalDateTime.now();

                if (lastFetch == null || now.isAfter(lastFetch.plus(FETCH_INTERVAL, ChronoUnit.MILLIS))) {
                    list = schedulingService.getAllSchedules(null, schedulingFiltering);
                    lastFetch = now;

                }
                for (Schedule schedule : list) {
                    try {
                        if (shouldRun(schedule, now)) {
                            logger.info("schedule time " + schedule.getName() + " has arrived");
                            schedulingService.runSchedule(schedule, securityContext);
                            logger.info("done running schedule " + schedule.getName());

                        }
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "failed running schedule " + schedule.getName());
                    }

                }
                Thread.sleep(CHECK_INTERVAL);

            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "interrupted while waiting for schedule check", e);
                stop = true;
            }
        }
    }

    private boolean shouldRun(Schedule schedule, LocalDateTime now) {
        return isInTimeFrame(schedule,now) && isDay(schedule,now)&&isTime(schedule,now);
    }

    private boolean isTime(Schedule schedule, LocalDateTime now) {
        LocalDateTime time=schedule.getTimeOfTheDay();
        if(schedule.getTimeOfTheDayName()!=null){
            time=schedulingService.getTimeFromName(schedule.getTimeOfTheDayName());
        }
        return time != null && time.getHour() == now.getHour();
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
