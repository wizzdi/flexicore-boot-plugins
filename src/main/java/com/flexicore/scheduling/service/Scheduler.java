package com.flexicore.scheduling.service;

import com.flexicore.scheduling.containers.request.SchedulingFiltering;
import com.flexicore.scheduling.init.Config;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleTimeslot;
import com.flexicore.scheduling.model.ScheduleToAction;
import com.flexicore.security.SecurityContext;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
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
	private static final long FETCH_INTERVAL = 5 * 60 * 1000;
	private static final long CHECK_INTERVAL = 60 * 1000;

	private boolean stop;
	private Logger logger;
	private SchedulingService schedulingService;
	private SecurityContext securityContext;
	private OffsetDateTime lastFetch = null;
	private ExecutorService executorService = Executors
			.newFixedThreadPool(Config.maxSchedulingThreads);

	public Scheduler(Logger logger, SchedulingService schedulingService,
			SecurityContext securityContext) {
		this.logger = logger;
		this.schedulingService = schedulingService;
		this.securityContext = securityContext;
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
					actionsMap = schedulingService
							.getAllScheduleLinks()
							.parallelStream()
							.filter(f -> f.getLeftside() != null
									&& !f.getLeftside().isSoftDelete())
							.collect(
									Collectors.groupingBy(f -> f.getLeftside()
											.getId(), Collectors.toList()));
					timeSlotsMap = schedulingService
							.getAllTimeSlots(actionsMap.keySet(), null)
							.parallelStream()
							.collect(
									Collectors.groupingBy(f -> f.getSchedule()
											.getId()));
					lastFetch = now;

				}
				try {
					Map<String, Boolean> currentlyRunningSchedules = new ConcurrentHashMap<>();
					for (Map.Entry<String, List<ScheduleToAction>> entry : actionsMap
							.entrySet()
							.parallelStream()
							.filter(f -> shouldRun(f.getValue().get(0)
									.getLeftside(), now))
							.collect(Collectors.toList())) {
						Schedule schedule = entry.getValue().get(0)
								.getLeftside();

						List<ScheduleTimeslot> timeslots = timeSlotsMap
								.get(schedule.getId());
						if (timeslots == null) {
							continue;
						}
						for (ScheduleTimeslot scheduleTimeslot : timeslots
								.parallelStream()
								.filter(f -> shouldRun(f, now)
										&& !currentlyRunningSchedules
												.getOrDefault(f.getId(), false))
								.collect(Collectors.toList())) {
							try {
								scheduleTimeslot.setLastExecution(OffsetDateTime
										.now());
								schedulingService.merge(scheduleTimeslot);
								executorService
										.execute(() -> {
											try {
												currentlyRunningSchedules.put(
														scheduleTimeslot
																.getId(), true);
												logger.info("schedule time "
														+ schedule.getName()
														+ "at timeslot- "
														+ scheduleTimeslot
																.getName()
														+ " has arrived");
												schedulingService.runSchedule(
														entry.getValue(),
														securityContext);
												logger.info("done running schedule "
														+ schedule.getName()
														+ " at timeslot "
														+ scheduleTimeslot
																.getName());
											} catch (Throwable e) {
												logger.log(Level.SEVERE,
														"failed executing ", e);
											} finally {
												currentlyRunningSchedules.put(
														scheduleTimeslot
																.getId(), false);

											}

										});

							} catch (Exception e) {
								logger.log(
										Level.SEVERE,
										"failed running schedule "
												+ schedule.getName(), e);
							}
						}

					}
				} catch (Exception e) {
					logger.log(Level.SEVERE,
							"exception while running schedule", e);
				}

				Thread.sleep(CHECK_INTERVAL);

			} catch (InterruptedException e) {
				logger.log(Level.WARNING,
						"interrupted while waiting for schedule check", e);
				stop = true;
			}
		}
		logger.info("Scheduler Stopped");

	}

	private boolean shouldRun(Schedule schedule, OffsetDateTime now) {
		return schedule.isEnabled() && isInTimeFrame(schedule, now)
				&& isDay(schedule, now);
	}

	private boolean shouldRun(ScheduleTimeslot scheduleTimeslot,
			OffsetDateTime now) {
		return isTime(scheduleTimeslot, now)
				&& checkPreviousRun(scheduleTimeslot, now);

	}

	private boolean checkPreviousRun(ScheduleTimeslot schedule,
			OffsetDateTime now) {
		if (schedule.getCoolDownIntervalBeforeRepeat() > 0) {
			return schedule.getLastExecution() == null
					|| schedule
							.getLastExecution()
							.plus(schedule.getCoolDownIntervalBeforeRepeat(),
									ChronoUnit.MILLIS).isBefore(now);
		} else {
			return schedule.getLastExecution() == null
					|| !schedule.getLastExecution().getDayOfWeek()
							.equals(now.getDayOfWeek());
		}

	}

	private boolean isTime(ScheduleTimeslot timeslot, OffsetDateTime now) {
		if ((timeslot.getStartTime() == null && timeslot
				.getStartTimeOfTheDayName() == null)
				|| (timeslot.getEndTime() == null && timeslot
						.getEndTimeOfTheDayName() == null)) {
			return false;
		}
		LocalTime nowTime = now.toLocalTime();
		String startZoneId = timeslot.getTimeStartZoneId() != null ? timeslot
				.getTimeStartZoneId() : "UTC";
		LocalTime start = timeslot.getStartTime() != null ? timeslot
				.getStartTime()
				.atZoneSameInstant(ZoneId.of(startZoneId)).toOffsetDateTime()
				.toLocalTime() : null;
		if (timeslot.getStartTimeOfTheDayName() != null) {
			start = schedulingService.getTimeFromName(
					timeslot.getStartTimeOfTheDayName(),
					timeslot.getTimeOfTheDayNameStartLon(),
					timeslot.getTimeOfTheDayNameStartLat()).orElse(null);
		}
		start = start != null ? start.plus(timeslot.getStartMillisOffset(),
				ChronoUnit.MILLIS) : null;
		String endZoneId = timeslot.getTimeEndZoneId() != null ? timeslot
				.getTimeEndZoneId() : "UTC";

		LocalTime end = timeslot.getEndTime() != null ? timeslot.getEndTime()
				.atZoneSameInstant(ZoneId.of(endZoneId)).toOffsetDateTime()
				.toLocalTime() : null;
		if (timeslot.getEndTimeOfTheDayName() != null) {
			end = schedulingService.getTimeFromName(
					timeslot.getEndTimeOfTheDayName(),
					timeslot.getTimeOfTheDayNameEndLon(),
					timeslot.getTimeOfTheDayNameEndLat()).orElse(null);

		}
		end = end != null ? end.plus(timeslot.getEndMillisOffset(),
				ChronoUnit.MILLIS) : null;

		return start != null && nowTime.isAfter(start)
				&& (end == null || nowTime.isBefore(end));
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
