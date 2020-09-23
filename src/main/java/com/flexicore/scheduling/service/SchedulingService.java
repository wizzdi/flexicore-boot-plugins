package com.flexicore.scheduling.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Read;
import com.flexicore.events.PluginsLoadedEvent;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Operation;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.User;
import com.flexicore.model.auditing.AuditingJob;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.scheduling.containers.request.*;
import com.flexicore.scheduling.containers.response.ExecuteScheduleResponse;
import com.flexicore.scheduling.data.SchedulingRepository;
import com.flexicore.scheduling.interfaces.ISchedulingService;
import com.flexicore.scheduling.model.*;
import com.flexicore.security.RunningUser;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.AuditingService;
import com.flexicore.service.DynamicInvokersService;
import com.flexicore.service.SecurityService;
import com.flexicore.service.UserService;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.calendar.astro.SolarTime;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.BadRequestException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
@Transactional
public class SchedulingService implements ISchedulingService {

	private static final Logger logger= LoggerFactory.getLogger(SchedulingService.class);

	@PluginInfo(version = 1)
	@Autowired
	private SchedulingRepository schedulingRepository;



	@Autowired
	private UserService userService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private AuditingService auditingService;

	@Autowired
	private DynamicInvokersService dynamicInvokersService;

	private static AtomicBoolean init = new AtomicBoolean(false);
	private static Scheduler scheduler;

	@EventListener
	public void init(PluginsLoadedEvent e) {
		if (init.compareAndSet(false, true)) {
			scheduler = new Scheduler( this, securityService);
			new Thread(scheduler).start();
		}
	}

	public SecurityContext getAdminSecurityContext() {
		User user = userService.getAdminUser();
		RunningUser runningUser = userService.registerUserIntoSystem(user,
				OffsetDateTime.now().plusYears(30));
		String adminToken = runningUser.getAuthenticationkey().getKey();
		return verifyLoggedIn(adminToken);
	}

	public SecurityContext verifyLoggedIn(String userToken) {
		String opId = Baseclass.generateUUIDFromString(Read.class
				.getCanonicalName());
		return securityService.getSecurityContext(userToken, null, opId);
	}

	public Schedule createSchedule(SecurityContext securityContext,
			CreateScheduling createScheduling) {
		Schedule schedule = createScheduleNoMerge(securityContext,
				createScheduling);
		schedulingRepository.merge(schedule);
		return schedule;

	}

	@Override
	public Schedule createScheduleNoMerge(SecurityContext securityContext,
			CreateScheduling createScheduling) {
		Schedule scheduling = new Schedule(createScheduling.getName(), securityContext);
		updateScheduleNoMerge(scheduling, createScheduling);
		return scheduling;

	}

	public boolean updateScheduleNoMerge(Schedule schedule,
			CreateScheduling createScheduling) {
		boolean update = false;
		if (createScheduling.getDescription() != null
				&& !createScheduling.getDescription().equals(
						schedule.getDescription())) {
			schedule.setDescription(createScheduling.getDescription());
			update = true;
		}

		if (createScheduling.getName() != null
				&& !createScheduling.getName().equals(schedule.getName())) {
			schedule.setName(createScheduling.getName());
			update = true;
		}
		if (createScheduling.getTimeFrameStart() != null
				&& !createScheduling.getTimeFrameStart().equals(
						schedule.getTimeFrameStart())) {
			schedule.setTimeFrameStart(createScheduling.getTimeFrameStart());
			update = true;
		}

		if (createScheduling.getTimeFrameEnd() != null
				&& !createScheduling.getTimeFrameEnd().equals(
						schedule.getTimeFrameEnd())) {
			schedule.setTimeFrameEnd(createScheduling.getTimeFrameEnd());
			update = true;
		}

		if (createScheduling.getSunday() != null
				&& !createScheduling.getSunday().equals(schedule.isSunday())) {
			schedule.setSunday(createScheduling.getSunday());
			update = true;
		}

		if (createScheduling.getMonday() != null
				&& !createScheduling.getMonday().equals(schedule.isMonday())) {
			schedule.setMonday(createScheduling.getMonday());
			update = true;
		}

		if (createScheduling.getTuesday() != null
				&& !createScheduling.getTuesday().equals(schedule.isTuesday())) {
			schedule.setTuesday(createScheduling.getTuesday());
			update = true;
		}

		if (createScheduling.getWednesday() != null
				&& !createScheduling.getWednesday().equals(
						schedule.isWednesday())) {
			schedule.setWednesday(createScheduling.getWednesday());
			update = true;
		}

		if (createScheduling.getThursday() != null
				&& !createScheduling.getThursday()
						.equals(schedule.isThursday())) {
			schedule.setThursday(createScheduling.getThursday());
			update = true;
		}

		if (createScheduling.getFriday() != null
				&& !createScheduling.getFriday().equals(schedule.isFriday())) {
			schedule.setFriday(createScheduling.getFriday());
			update = true;
		}

		if (createScheduling.getSaturday() != null
				&& !createScheduling.getSaturday()
						.equals(schedule.isSaturday())) {
			schedule.setSaturday(createScheduling.getSaturday());
			update = true;
		}

		if (createScheduling.getHoliday() != null
				&& !createScheduling.getHoliday().equals(schedule.isHoliday())) {
			schedule.setHoliday(createScheduling.getHoliday());
			update = true;
		}

		if (createScheduling.getEnabled() != null
				&& !createScheduling.getEnabled().equals(schedule.isEnabled())) {
			schedule.setEnabled(createScheduling.getEnabled());
			update = true;
		}
		return update;
	}

	public ScheduleAction createScheduleAction(SecurityContext securityContext,
			CreateSchedulingAction createSchedulingAction) {
		ScheduleAction scheduleAction = createScheduleActionNoMerge(
				securityContext, createSchedulingAction);
		schedulingRepository.merge(scheduleAction);
		return scheduleAction;
	}

	@Override
	public ScheduleAction createScheduleActionNoMerge(
			SecurityContext securityContext,
			CreateSchedulingAction createSchedulingAction) {
		ScheduleAction scheduleAction = new ScheduleAction(
				createSchedulingAction.getName(), securityContext);
		updateScheduleActionNoMerge(scheduleAction, createSchedulingAction);
		return scheduleAction;
	}

	public boolean updateScheduleActionNoMerge(ScheduleAction scheduleAction,
			CreateSchedulingAction createSchedulingAction) {
		boolean update = false;
		if (createSchedulingAction.getName() != null
				&& !createSchedulingAction.getName().equals(
						scheduleAction.getName())) {
			scheduleAction.setName(createSchedulingAction.getName());
			update = true;
		}
		if (createSchedulingAction.getDescription() != null
				&& !createSchedulingAction.getDescription().equals(
						scheduleAction.getDescription())) {
			scheduleAction.setDescription(createSchedulingAction
					.getDescription());
			update = true;
		}
		if (createSchedulingAction.getDynamicExecution() != null
				&& (scheduleAction.getDynamicExecution() == null || !createSchedulingAction
						.getDynamicExecution().getId()
						.equals(scheduleAction.getDynamicExecution().getId()))) {
			scheduleAction.setDynamicExecution(createSchedulingAction
					.getDynamicExecution());
			update = true;
		}
		return update;

	}

	public List<Schedule> getAllSchedules(SecurityContext securityContext,
			SchedulingFiltering filtering) {
		QueryInformationHolder<Schedule> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Schedule.class, securityContext);
		return schedulingRepository.getAllFiltered(queryInformationHolder);
	}

	public List<ScheduleToAction> getAllScheduleLinks() {
		return schedulingRepository.getAllSchedullingLinks(
				new LinkScheduleToAction(), null);
	}

	public ScheduleToAction unlinkScheduleToAction(
			SecurityContext securityContext,
			LinkScheduleToAction linkScheduleToAction) {

		List<ScheduleToAction> links = schedulingRepository
				.getAllSchedullingLinks(linkScheduleToAction, securityContext);
		for (ScheduleToAction link : links) {
			link.setSoftDelete(true);
		}
		for (ScheduleToAction link : links) {
			schedulingRepository.merge(link);
		}
		return links.isEmpty() ? null : links.get(0);
	}

	public ScheduleTimeslot createScheduleTimeSlot(
			SecurityContext securityContext, CreateTimeslot createTimeslot) {
		ScheduleTimeslot scheduleTimeslot = createScheduleTimeSlotNoMerge(
				securityContext, createTimeslot);
		schedulingRepository.merge(scheduleTimeslot);
		return scheduleTimeslot;
	}

	@Override
	public ScheduleTimeslot createScheduleTimeSlotNoMerge(
			SecurityContext securityContext, CreateTimeslot createTimeslot) {
		ScheduleTimeslot scheduleTimeslot = new ScheduleTimeslot(createTimeslot.getName(), securityContext);
		updateScheduleTimeslot(scheduleTimeslot, createTimeslot);
		return scheduleTimeslot;
	}

	public ScheduleTimeslot updateScheduleTimeSlot(
			SecurityContext securityContext, UpdateTimeslot updateTimeslot) {
		ScheduleTimeslot scheduleTimeslot = updateTimeslot
				.getScheduleTimeslot();
		if (updateScheduleTimeslot(scheduleTimeslot, updateTimeslot)) {
			schedulingRepository.merge(scheduleTimeslot);

		}
		return scheduleTimeslot;
	}

	private boolean updateScheduleTimeslot(ScheduleTimeslot scheduleTimeslot,
			CreateTimeslot createTimeslot) {
		boolean update = false;
		if (createTimeslot.getName() != null
				&& !createTimeslot.getName().equals(scheduleTimeslot.getName())) {
			scheduleTimeslot.setName(createTimeslot.getName());
			update = true;
		}

		if (createTimeslot.getDescription() != null
				&& !createTimeslot.getDescription().equals(
						scheduleTimeslot.getDescription())) {
			scheduleTimeslot.setDescription(createTimeslot.getDescription());
			update = true;
		}
		if (createTimeslot.getTimeOfTheDayStart() != null && !createTimeslot.getTimeOfTheDayStart().equals(scheduleTimeslot.getStartTime())) {
				scheduleTimeslot.setStartTime(createTimeslot.getTimeOfTheDayStart());
				scheduleTimeslot.setStartTimeOffsetId(createTimeslot.getTimeOfTheDayStart().getOffset().getId());
				scheduleTimeslot.setStartTimeOfTheDayName(null);
				update = true;
		}

		if (createTimeslot.getTimeOfTheDayEnd() != null && !createTimeslot.getTimeOfTheDayEnd().equals(scheduleTimeslot.getEndTime())) {
			scheduleTimeslot.setEndTime(createTimeslot.getTimeOfTheDayEnd());
			scheduleTimeslot.setEndTimeOffsetId(createTimeslot.getTimeOfTheDayEnd().getOffset().getId());
			scheduleTimeslot.setEndTimeOfTheDayName(null);
			update = true;
		}


		if (createTimeslot.getCoolDownIntervalBeforeRepeat() != null
				&& !createTimeslot.getCoolDownIntervalBeforeRepeat().equals(
						scheduleTimeslot.getCoolDownIntervalBeforeRepeat())) {
			scheduleTimeslot.setCoolDownIntervalBeforeRepeat(createTimeslot
					.getCoolDownIntervalBeforeRepeat());
			update = true;
		}

		if (createTimeslot.getStartTimeOfTheDayName() != null
				&& !createTimeslot.getStartTimeOfTheDayName().equals(
						scheduleTimeslot.getStartTimeOfTheDayName())) {
			scheduleTimeslot.setStartTimeOfTheDayName(createTimeslot
					.getStartTimeOfTheDayName());
			scheduleTimeslot.setStartTime(null);
			update = true;
		}

		if (createTimeslot.getTimeOfTheDayNameStartLat() != null
				&& !createTimeslot.getTimeOfTheDayNameStartLat().equals(
						scheduleTimeslot.getTimeOfTheDayNameStartLat())) {
			scheduleTimeslot.setTimeOfTheDayNameStartLat(createTimeslot
					.getTimeOfTheDayNameStartLat());
			update = true;
		}

		if (createTimeslot.getTimeOfTheDayNameStartLon() != null
				&& !createTimeslot.getTimeOfTheDayNameStartLon().equals(
						scheduleTimeslot.getTimeOfTheDayNameStartLon())) {
			scheduleTimeslot.setTimeOfTheDayNameStartLon(createTimeslot
					.getTimeOfTheDayNameStartLon());
			update = true;
		}

		if (createTimeslot.getEndTimeOfTheDayName() != null
				&& !createTimeslot.getEndTimeOfTheDayName().equals(
						scheduleTimeslot.getEndTimeOfTheDayName())) {
			scheduleTimeslot.setEndTimeOfTheDayName(createTimeslot
					.getEndTimeOfTheDayName());
			scheduleTimeslot.setEndTime(null);
			update = true;
		}

		if (createTimeslot.getTimeOfTheDayNameEndLat() != null
				&& !createTimeslot.getTimeOfTheDayNameEndLat().equals(
						scheduleTimeslot.getTimeOfTheDayNameEndLat())) {
			scheduleTimeslot.setTimeOfTheDayNameEndLat(createTimeslot
					.getTimeOfTheDayNameEndLat());
			update = true;
		}

		if (createTimeslot.getTimeOfTheDayNameEndLon() != null
				&& !createTimeslot.getTimeOfTheDayNameEndLon().equals(
						scheduleTimeslot.getTimeOfTheDayNameEndLon())) {
			scheduleTimeslot.setTimeOfTheDayNameEndLon(createTimeslot
					.getTimeOfTheDayNameEndLon());
			update = true;
		}

		if (createTimeslot.getCoolDownIntervalBeforeRepeat() != null
				&& !createTimeslot.getCoolDownIntervalBeforeRepeat().equals(
						scheduleTimeslot.getCoolDownIntervalBeforeRepeat())) {
			scheduleTimeslot.setCoolDownIntervalBeforeRepeat(createTimeslot
					.getCoolDownIntervalBeforeRepeat());
			update = true;
		}

		if (createTimeslot.getStartMillisOffset() != null
				&& !createTimeslot.getStartMillisOffset().equals(
						scheduleTimeslot.getStartMillisOffset())) {
			scheduleTimeslot.setStartMillisOffset(createTimeslot
					.getStartMillisOffset());
			update = true;
		}

		if (createTimeslot.getEndMillisOffset() != null
				&& !createTimeslot.getEndMillisOffset().equals(
						scheduleTimeslot.getEndMillisOffset())) {
			scheduleTimeslot.setEndMillisOffset(createTimeslot
					.getEndMillisOffset());
			update = true;
		}

		if (createTimeslot.getSchedule() != null
				&& (scheduleTimeslot.getSchedule() == null || !createTimeslot
						.getSchedule().getId()
						.equals(scheduleTimeslot.getSchedule().getId()))) {
			scheduleTimeslot.setSchedule(createTimeslot.getSchedule());
			update = true;
		}

		return update;
	}

	public Optional<OffsetTime> getTimeFromName(
			TimeOfTheDayName timeOfTheDayName, double lon, double lat) {
		SolarTime solarTime;
		Optional<Moment> result = Optional.empty();
		switch (timeOfTheDayName) {
			case SUNRISE :
				solarTime = SolarTime.ofLocation(lat, lon);
				result = PlainDate.nowInSystemTime().get(solarTime.sunrise());
				break;
			case SUNSET :
				solarTime = SolarTime.ofLocation(lat, lon);
				result = PlainDate.nowInSystemTime().get(solarTime.sunset());
				break;
		}

		return result.map(f -> f.toLocalTimestamp().toTemporalAccessor()
				.atZone(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime());

	}

	@Transactional
	public void runSchedule(List<ScheduleToAction> schedule,
			SecurityContext securityContext) {
		for (ScheduleToAction link : schedule) {

			long start = System.currentTimeMillis();
			boolean failed = false;
			Object response = null;
			ScheduleAction scheduleAction = link.getRightside();
			try {
				DynamicExecution dynamicExecution = scheduleAction
						.getDynamicExecution();
				if (dynamicExecution != null) {
					response = dynamicInvokersService.executeInvoker(
							dynamicInvokersService.getExecuteInvokerRequest(
									dynamicExecution, securityContext),
							securityContext);
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
				auditSchedule(securityContext, scheduleAction, start, failed,
						response);
			}

		}

	}

	private void auditSchedule(SecurityContext securityContext,
			ScheduleAction scheduleAction, long start, boolean failed,
			Object response) {
		long timeTaken = System.currentTimeMillis() - start;
		SecurityContext securityContextForAuditing = getSecurityContextForAuditing(
				securityContext, scheduleAction);
		auditingService.addAuditingJob(new AuditingJob()
				.setDateOccured(Date.from(Instant.now()))
				.setResponse(response)
				.setTimeTaken(timeTaken)
				.setSecurityContext(securityContextForAuditing)
				.setInvocationContext(
						new SchduelingInvocationContext(new Object[]{
								scheduleAction, securityContext}))
				.setAuditingType(SchedulingAuditTypes.SCHEDULING.name())
				.setFailed(failed));
	}

	private SecurityContext getSecurityContextForAuditing(
			SecurityContext securityContext, ScheduleAction scheduleAction) {
		SecurityContext securityContextForAuditing = new SecurityContext(
				securityContext.getTenants(), securityContext.getUser(),
				securityContext.getOperation(),
				securityContext.getTenantToCreateIn());
		Operation schedulingOperation = new Operation();
		schedulingOperation.setId(scheduleAction.getId());
		schedulingOperation.setName(scheduleAction.getName());
		schedulingOperation.setDescription(scheduleAction.getDescription());
		securityContextForAuditing.setOperation(schedulingOperation);
		return securityContext;
	}

	private boolean matchParameters(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		return method.getParameterCount() == 2
				&& ScheduleAction.class.isAssignableFrom(parameterTypes[0])
				&& parameterTypes[1] == SecurityContext.class;
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batchString, SecurityContext securityContext) {
		return schedulingRepository.getByIdOrNull(id, c, batchString,
				securityContext);
	}

	public ScheduleToAction linkScheduleToAction(
			SecurityContext securityContext,
			LinkScheduleToAction createScheduling) {
		ScheduleToAction scheduleToAction = linkScheduleToActionNoMerge(
				securityContext, createScheduling);
		schedulingRepository.merge(scheduleToAction);
		return scheduleToAction;
	}

	@Override
	public ScheduleToAction linkScheduleToActionNoMerge(
			SecurityContext securityContext,
			LinkScheduleToAction createScheduling) {
		ScheduleToAction scheduleToAction = new ScheduleToAction("link", securityContext);
		scheduleToAction.setLeftside(createScheduling.getSchedule());
		scheduleToAction.setRightside(createScheduling.getScheduleAction());
		return scheduleToAction;
	}

	public List<ScheduleAction> getAllScheduleActions(
			SecurityContext securityContext, SchedulingActionFiltering filtering) {
		return schedulingRepository.getAllScheduleActions(filtering,
				securityContext);
	}

	public Schedule updateSchedule(SecurityContext securityContext,
			UpdateScheduling updateScheduling) {
		if (updateScheduleNoMerge(updateScheduling.getSchedule(),
				updateScheduling)) {
			schedulingRepository.merge(updateScheduling.getSchedule());
		}
		return updateScheduling.getSchedule();
	}

	public ScheduleAction updateScheduleAction(SecurityContext securityContext,
			UpdateSchedulingAction updateSchedulingAction) {
		List<Object> toMerge = new ArrayList<>();
		if (updateScheduleActionNoMerge(
				updateSchedulingAction.getScheduleAction(),
				updateSchedulingAction)) {
			toMerge.add(updateSchedulingAction.getScheduleAction());
			schedulingRepository.massMerge(toMerge);
		}
		return updateSchedulingAction.getScheduleAction();
	}

	@Override
	public void massMerge(List<?> toMerge) {
		schedulingRepository.massMerge(toMerge);
	}

	public void merge(Object o) {
		schedulingRepository.merge(o);
	}

	public List<ScheduleTimeslot> getAllTimeSlots(Set<String> scheduleIds,
			SecurityContext securityContext) {
		return scheduleIds.isEmpty() ? new ArrayList<>() : schedulingRepository
				.getAllTimeSlots(scheduleIds, securityContext);
	}

	public List<ScheduleTimeslot> getAllScheduleTimeslots(
			SecurityContext securityContext,
			SchedulingTimeslotFiltering filtering) {
		return schedulingRepository.getAllScheduleTimeslots(filtering,
				securityContext);
	}

	public ExecuteScheduleResponse executeScheduleNow(
			SecurityContext securityContext,
			ExecuteScheduleRequest executeScheduleRequest) {
		Map<String, List<ScheduleToAction>> actionsMap = getAllScheduleLinks()
				.parallelStream()
				.filter(f -> f.getLeftside() != null)
				.collect(
						Collectors.groupingBy(f -> f.getLeftside().getId(),
								Collectors.toList()));
		List<ScheduleToAction> actions = actionsMap
				.getOrDefault(executeScheduleRequest.getSchedule().getId(),
						new ArrayList<>());
		new Thread(() -> {
			runSchedule(actions, securityContext);
		}).start();
		return new ExecuteScheduleResponse().setExecutedLinks(actions
				.parallelStream().map(f -> f.getId())
				.collect(Collectors.toSet()));

	}

	public void validateSchedulingAction(
			CreateSchedulingAction createScheduling,
			SecurityContext securityContext) {
		String dynamicExecutionId = createScheduling.getDynamicExecutionId();
		DynamicExecution dynamicExecution = dynamicExecutionId != null
				? getByIdOrNull(dynamicExecutionId, DynamicExecution.class,
						null, securityContext) : null;
		if (dynamicExecution == null && dynamicExecutionId != null) {
			throw new BadRequestException("No Dynamic Execution With id "
					+ dynamicExecutionId);
		}
		createScheduling.setDynamicExecution(dynamicExecution);
	}
}
