package com.flexicore.scheduling.service;

import com.flexicore.scheduling.init.Config;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleTimeslot;
import com.flexicore.scheduling.model.ScheduleToAction;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Scheduler implements Runnable {
	private static final long FETCH_INTERVAL = 5 * 60 * 1000;
	private static final long CHECK_INTERVAL = 60 * 1000;
	private static final Logger logger= LoggerFactory.getLogger(Scheduler.class);

	private boolean stop;
	private final SchedulingService schedulingService;
	private final SecurityService securityService;
	private OffsetDateTime lastFetch = null;
	private final ExecutorService executorService = Executors
			.newFixedThreadPool(Config.maxSchedulingThreads);

	public Scheduler(SchedulingService schedulingService,SecurityService securityService) {
		this.schedulingService = schedulingService;
		this.securityService=securityService;
	}

	@Override
	public void run() {
		Map<String, List<ScheduleToAction>> actionsMap = new HashMap<>();
		Map<String, List<ScheduleTimeslot>> timeSlotsMap = new HashMap<>();
		logger.info("Scheduler Started");
		while (!stop) {
			try {
				OffsetDateTime now = OffsetDateTime.now();

				if (lastFetch == null
						|| now.isAfter(lastFetch.plus(FETCH_INTERVAL,
								ChronoUnit.MILLIS))) {
					actionsMap = schedulingService.getAllScheduleLinks().parallelStream().filter(f -> f.getLeftside() != null && !f.getLeftside().isSoftDelete()).collect(Collectors.groupingBy(f -> f.getLeftside().getId(), Collectors.toList()));
					timeSlotsMap = schedulingService.getAllTimeSlots(actionsMap.keySet(), null).parallelStream().collect(Collectors.groupingBy(f -> f.getSchedule().getId()));
					lastFetch = now;
					logger.debug("Fetched Actions");

				}
				try {
					Map<String, Boolean> currentlyRunningSchedules = new ConcurrentHashMap<>();
					if(actionsMap.isEmpty()){
						logger.debug("No Actions");
					}
					List<Map.Entry<String, List<ScheduleToAction>>> filteredActions = actionsMap.entrySet().parallelStream().filter(f -> shouldRun(f.getValue().get(0).getLeftside(), now)).collect(Collectors.toList());
					if(filteredActions.isEmpty() && !actionsMap.isEmpty()){
						logger.debug("No Filtered Actions");

					}
					for (Map.Entry<String, List<ScheduleToAction>> entry : filteredActions) {
						Schedule schedule = entry.getValue().get(0).getLeftside();
						SecurityContext scheduleSecurityContext=securityService.getUserSecurityContext(schedule.getCreator());
						List<ScheduleTimeslot> timeslots = timeSlotsMap.get(schedule.getId());
						if (timeslots == null) {
							logger.debug("no timeslots for schedule "+schedule.getName() +"("+schedule.getId()+")");
							continue;
						}
						for (ScheduleTimeslot scheduleTimeslot : timeslots.parallelStream().filter(f -> shouldRun(f, now) && !currentlyRunningSchedules.getOrDefault(f.getId(), false)).collect(Collectors.toList())) {
							try {
								scheduleTimeslot.setLastExecution(OffsetDateTime.now());
								schedulingService.merge(scheduleTimeslot);
								executorService.execute(() -> {
											try {
												currentlyRunningSchedules.put(scheduleTimeslot.getId(), true);
												logger.info("schedule time " + schedule.getName() + "at timeslot- " + scheduleTimeslot
														.getName() +" as user "+scheduleSecurityContext.getUser().getName()+"("+scheduleSecurityContext.getUser().getId()+")"+" has arrived");
												schedulingService.runSchedule(entry.getValue(), scheduleSecurityContext);
												logger.info("done running schedule " + schedule.getName() + " at timeslot " + scheduleTimeslot
														.getName());
											} catch (Throwable e) {
												logger.error( "failed executing ", e);
											} finally {
												currentlyRunningSchedules.put(scheduleTimeslot.getId(), false);

											}

										});

							} catch (Exception e) {
								logger.error( "failed running schedule " + schedule.getName(), e);
							}
						}

					}
				} catch (Exception e) {
					logger.error( "exception while running schedule", e);
				}

				Thread.sleep(CHECK_INTERVAL);

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
		if(!shouldRun){
			logger.debug("schedule "+schedule.getName() +"("+schedule.getId()+") should not run , in timespan: "+inTimeFrame +" , day: "+day+", enabled:"+enabled);
		}
		return shouldRun;
	}

	private boolean shouldRun(ScheduleTimeslot scheduleTimeslot, OffsetDateTime now) {
		boolean time = isTime(scheduleTimeslot, now);
		boolean previousRun = checkPreviousRun(scheduleTimeslot, now);
		boolean shouldRun = time && previousRun;
		if(!shouldRun){
			logger.debug("schedule timeslot "+scheduleTimeslot.getName() +"("+scheduleTimeslot.getId()+") should not run , in timespan: "+time +" , previous run: "+previousRun);

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
			start = schedulingService.getTimeFromName(timeslot.getStartTimeOfTheDayName(), timeslot.getTimeOfTheDayNameStartLon(), timeslot.getTimeOfTheDayNameStartLat()).orElse(null);
		}
		start = start != null ? start.plus(timeslot.getStartMillisOffset(),
				ChronoUnit.MILLIS) : null;

		OffsetTime end = timeslot.getEndTime() != null ? timeslot.getEndTime().withOffsetSameInstant(ZoneOffset.of(timeslot.getEndTimeOffsetId())).toOffsetTime() : null;
		if (timeslot.getEndTimeOfTheDayName() != null) {
			end = schedulingService.getTimeFromName(timeslot.getEndTimeOfTheDayName(), timeslot.getTimeOfTheDayNameEndLon(), timeslot.getTimeOfTheDayNameEndLat()).orElse(null);

		}
		end = end != null ? end.plus(timeslot.getEndMillisOffset(), ChronoUnit.MILLIS) : null;

		return start != null && nowTime.isAfter(start) && (end == null || nowTime.isBefore(end));
	}

	private boolean isInTimeFrame(Schedule schedule, OffsetDateTime now) {
		return now.isAfter(schedule.getTimeFrameStart())
				&& now.isBefore(schedule.getTimeFrameEnd());
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
}
