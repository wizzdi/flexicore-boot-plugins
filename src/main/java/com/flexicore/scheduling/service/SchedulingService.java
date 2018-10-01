package com.flexicore.scheduling.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Read;
import com.flexicore.interfaces.InitPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.*;
import com.flexicore.model.auditing.AuditingJob;
import com.flexicore.request.ExecuteInvokerRequest;
import com.flexicore.scheduling.containers.request.*;
import com.flexicore.scheduling.data.SchedulingRepository;
import com.flexicore.scheduling.model.*;
import com.flexicore.security.RunningUser;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.calendar.astro.SolarTime;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@PluginInfo(version = 1, autoInstansiate = true)
public class SchedulingService implements ServicePlugin, InitPlugin {


    @Inject
    @PluginInfo(version = 1)
    private SchedulingRepository schedulingRepository;

    @Inject
    private PluginService pluginService;

    @Inject
    private Logger logger;

    @Inject
    private UserService userService;

    @Inject
    private SecurityService securityService;

    @Inject
    private AuditingService auditingService;

    @Inject
    private DynamicInvokersService dynamicInvokersService;

    private static AtomicBoolean init = new AtomicBoolean(false);
    private static Scheduler scheduler;
    private static Cache<String, Method> methodCache = CacheBuilder.newBuilder().maximumSize(100).build();

    @Override
    public void init() {
        if (init.compareAndSet(false, true)) {
            SecurityContext securityContext = getAdminSecurityContext();
            scheduler = new Scheduler(logger, this, securityContext);
            new Thread(scheduler).start();
        }
    }

    public SecurityContext getAdminSecurityContext() {
        User user = userService.getAdminUser();
        RunningUser runningUser = userService.registerUserIntoSystem(user, LocalDateTime.now().plusYears(30));
        String adminToken = runningUser.getAuthenticationkey().getKey();
        return verifyLoggedIn(adminToken);
    }

    public SecurityContext verifyLoggedIn(String userToken) {
        String opId = Baseclass.generateUUIDFromString(Read.class.getCanonicalName());
        return securityService.getSecurityContext(userToken, null, opId);
    }

    public Schedule createSchedule(SecurityContext securityContext, CreateScheduling createScheduling) {
        Schedule schedule = createScheduleNoMerge(securityContext, createScheduling);
        schedulingRepository.merge(schedule);
        return schedule;

    }

    public Schedule createScheduleNoMerge(SecurityContext securityContext, CreateScheduling createScheduling) {
        Schedule scheduling = Schedule.s().CreateUnchecked(createScheduling.getName(), securityContext);
        scheduling.Init();
        updateScheduleNoMerge(scheduling, createScheduling);
        return scheduling;

    }

    public boolean updateScheduleNoMerge(Schedule schedule, CreateScheduling createScheduling) {
        boolean update = false;
        if (createScheduling.getDescription() != null && !createScheduling.getDescription().equals(schedule.getDescription())) {
            schedule.setDescription(createScheduling.getDescription());
            update = true;
        }

        if (createScheduling.getName() != null && !createScheduling.getName().equals(schedule.getName())) {
            schedule.setName(createScheduling.getName());
            update = true;
        }
        if (createScheduling.getTimeFrameStart() != null && !createScheduling.getTimeFrameStart().equals(schedule.getTimeFrameStart())) {
            schedule.setTimeFrameStart(createScheduling.getTimeFrameStart());
            update = true;
        }

        if (createScheduling.getTimeFrameEnd() != null && !createScheduling.getTimeFrameEnd().equals(schedule.getTimeFrameEnd())) {
            schedule.setTimeFrameEnd(createScheduling.getTimeFrameEnd());
            update = true;
        }

        if (createScheduling.getSunday() != null && !createScheduling.getSunday().equals(schedule.isSunday())) {
            schedule.setSunday(createScheduling.getSunday());
            update = true;
        }

        if (createScheduling.getMonday() != null && !createScheduling.getMonday().equals(schedule.isMonday())) {
            schedule.setMonday(createScheduling.getMonday());
            update = true;
        }

        if (createScheduling.getTuesday() != null && !createScheduling.getTuesday().equals(schedule.isTuesday())) {
            schedule.setTuesday(createScheduling.getTuesday());
            update = true;
        }

        if (createScheduling.getWednesday() != null && !createScheduling.getWednesday().equals(schedule.isWednesday())) {
            schedule.setWednesday(createScheduling.getWednesday());
            update = true;
        }

        if (createScheduling.getThursday() != null && !createScheduling.getThursday().equals(schedule.isThursday())) {
            schedule.setThursday(createScheduling.getThursday());
            update = true;
        }

        if (createScheduling.getFriday() != null && !createScheduling.getFriday().equals(schedule.isFriday())) {
            schedule.setFriday(createScheduling.getFriday());
            update = true;
        }

        if (createScheduling.getSaturday() != null && !createScheduling.getSaturday().equals(schedule.isSaturday())) {
            schedule.setSaturday(createScheduling.getSaturday());
            update = true;
        }

        if (createScheduling.getHoliday() != null && !createScheduling.getHoliday().equals(schedule.isHoliday())) {
            schedule.setHoliday(createScheduling.getHoliday());
            update = true;
        }

        if (createScheduling.getEnabled() != null && !createScheduling.getEnabled().equals(schedule.isEnabled())) {
            schedule.setEnabled(createScheduling.getEnabled());
            update = true;
        }
        return update;
    }

    public ScheduleAction createScheduleAction(SecurityContext securityContext, CreateSchedulingAction createSchedulingAction) {
        ScheduleAction scheduleAction = createScheduleActionNoMerge(securityContext, createSchedulingAction);
        schedulingRepository.merge(scheduleAction);
        return scheduleAction;
    }

    public ScheduleAction createScheduleActionNoMerge(SecurityContext securityContext, CreateSchedulingAction createSchedulingAction) {
        ScheduleAction scheduleAction = ScheduleAction.s().CreateUnchecked(createSchedulingAction.getName(), securityContext);
        scheduleAction.Init();

        updateScheduleActionNoMerge(scheduleAction, createSchedulingAction);
        return scheduleAction;
    }

    public boolean updateScheduleActionNoMerge(ScheduleAction scheduleAction, CreateSchedulingAction createSchedulingAction) {
        boolean update = false;
        if (createSchedulingAction.getMethodName() != null && !createSchedulingAction.getMethodName().equals(scheduleAction.getMethodName())) {
            scheduleAction.setMethodName(createSchedulingAction.getMethodName());
            update = true;
        }

        if (createSchedulingAction.getName() != null && !createSchedulingAction.getName().equals(scheduleAction.getName())) {
            scheduleAction.setName(createSchedulingAction.getName());
            update = true;
        }

        if (createSchedulingAction.getDescription() != null && !createSchedulingAction.getDescription().equals(scheduleAction.getDescription())) {
            scheduleAction.setDescription(createSchedulingAction.getDescription());
            update = true;
        }



        if (createSchedulingAction.getServiceCanonicalNames() != null ) {
            Set<String> ids = scheduleAction.getServiceCanonicalNames().parallelStream().map(f -> f.getServiceCanonicalName()).collect(Collectors.toSet());
            createSchedulingAction.getServiceCanonicalNames().removeAll(ids);
            if(!createSchedulingAction.getServiceCanonicalNames().isEmpty()){
                scheduleAction.getServiceCanonicalNames().addAll(createSchedulingAction.getServiceCanonicalNames().parallelStream()
                        .map(f->new ServiceCanonicalName()
                                .setId(Baseclass.getBase64ID())
                                .setScheduleAction(scheduleAction)
                                .setServiceCanonicalName(f)
                        )
                        .collect(Collectors.toList())
                );
                update = true;
            }


        }
        if (createSchedulingAction.getExecutionParametersHolder() != null) {
            createSchedulingAction.getExecutionParametersHolder().setId(Baseclass.getBase64ID());
            scheduleAction.setExecutionParametersHolder(createSchedulingAction.getExecutionParametersHolder());

        }
        return update;

    }

    public List<Schedule> getAllSchedules(SecurityContext securityContext, SchedulingFiltering filtering) {
        QueryInformationHolder<Schedule> queryInformationHolder = new QueryInformationHolder<>(filtering, Schedule.class, securityContext);
        return schedulingRepository.getAllFiltered(queryInformationHolder);
    }

    public List<ScheduleToAction> getAllScheduleLinks() {
        return schedulingRepository.getAll(ScheduleToAction.class);
    }

    public ScheduleToAction unlinkScheduleToAction(SecurityContext securityContext, LinkScheduleToAction linkScheduleToAction) {

        List<ScheduleToAction> links = schedulingRepository.getAllSchedullingLinks(linkScheduleToAction, securityContext);
        for (ScheduleToAction link : links) {
            link.setSoftDelete(true);
        }
        for (ScheduleToAction link : links) {
            schedulingRepository.merge(link);
        }
        return links.isEmpty() ? null : links.get(0);
    }

    public ScheduleTimeslot createScheduleTimeSlot(SecurityContext securityContext, CreateTimeslot createTimeslot) {
        ScheduleTimeslot scheduleTimeslot = ScheduleTimeslot.s().CreateUnchecked(createTimeslot.getName(), securityContext);
        scheduleTimeslot.Init();
        updateScheduleTimeslot(scheduleTimeslot, createTimeslot);
        schedulingRepository.merge(scheduleTimeslot);
        return scheduleTimeslot;
    }


    public ScheduleTimeslot updateScheduleTimeSlot(SecurityContext securityContext, UpdateTimeslot updateTimeslot) {
        ScheduleTimeslot scheduleTimeslot = updateTimeslot.getScheduleTimeslot();
        if (updateScheduleTimeslot(scheduleTimeslot, updateTimeslot)) {
            schedulingRepository.merge(scheduleTimeslot);

        }
        return scheduleTimeslot;
    }

    private boolean updateScheduleTimeslot(ScheduleTimeslot scheduleTimeslot, CreateTimeslot createTimeslot) {
        boolean update = false;
        if (createTimeslot.getName() != null && !createTimeslot.getName().equals(scheduleTimeslot.getName())) {
            scheduleTimeslot.setName(createTimeslot.getName());
            update = true;
        }

        if (createTimeslot.getDescription() != null && !createTimeslot.getDescription().equals(scheduleTimeslot.getDescription())) {
            scheduleTimeslot.setDescription(createTimeslot.getDescription());
            update = true;
        }
        if (createTimeslot.getTimeOfTheDayStart() != null && !createTimeslot.getTimeOfTheDayStart().equals(scheduleTimeslot.getStartTime())) {
            scheduleTimeslot.setStartTime(createTimeslot.getTimeOfTheDayStart());
            update = true;
        }

        if (createTimeslot.getTimeOfTheDayEnd() != null && !createTimeslot.getTimeOfTheDayEnd().equals(scheduleTimeslot.getEndTime())) {
            scheduleTimeslot.setEndTime(createTimeslot.getTimeOfTheDayEnd());
            update = true;
        }

        if (createTimeslot.getCoolDownIntervalBeforeRepeat() != null && !createTimeslot.getCoolDownIntervalBeforeRepeat().equals(scheduleTimeslot.getCoolDownIntervalBeforeRepeat())) {
            scheduleTimeslot.setCoolDownIntervalBeforeRepeat(createTimeslot.getCoolDownIntervalBeforeRepeat());
            update = true;
        }

        if (createTimeslot.getStartTimeOfTheDayName() != null && !createTimeslot.getStartTimeOfTheDayName().equals(scheduleTimeslot.getStartTimeOfTheDayName())) {
            scheduleTimeslot.setStartTimeOfTheDayName(createTimeslot.getStartTimeOfTheDayName());
            update = true;
        }

        if (createTimeslot.getTimeOfTheDayNameStartLat() != null && !createTimeslot.getTimeOfTheDayNameStartLat().equals(scheduleTimeslot.getTimeOfTheDayNameStartLat())) {
            scheduleTimeslot.setTimeOfTheDayNameStartLat(createTimeslot.getTimeOfTheDayNameStartLat());
            update = true;
        }

        if (createTimeslot.getTimeOfTheDayNameStartLon() != null && !createTimeslot.getTimeOfTheDayNameStartLon().equals(scheduleTimeslot.getTimeOfTheDayNameStartLon())) {
            scheduleTimeslot.setTimeOfTheDayNameStartLon(createTimeslot.getTimeOfTheDayNameStartLon());
            update = true;
        }

        if (createTimeslot.getEndTimeOfTheDayName() != null && !createTimeslot.getEndTimeOfTheDayName().equals(scheduleTimeslot.getEndTimeOfTheDayName())) {
            scheduleTimeslot.setEndTimeOfTheDayName(createTimeslot.getEndTimeOfTheDayName());
            update = true;
        }

        if (createTimeslot.getTimeOfTheDayNameEndLat() != null && !createTimeslot.getTimeOfTheDayNameEndLat().equals(scheduleTimeslot.getTimeOfTheDayNameEndLat())) {
            scheduleTimeslot.setTimeOfTheDayNameEndLat(createTimeslot.getTimeOfTheDayNameEndLat());
            update = true;
        }

        if (createTimeslot.getTimeOfTheDayNameEndLon() != null && !createTimeslot.getTimeOfTheDayNameEndLon().equals(scheduleTimeslot.getTimeOfTheDayNameEndLon())) {
            scheduleTimeslot.setTimeOfTheDayNameEndLon(createTimeslot.getTimeOfTheDayNameEndLon());
            update = true;
        }

        if (createTimeslot.getCoolDownIntervalBeforeRepeat() != null && !createTimeslot.getCoolDownIntervalBeforeRepeat().equals(scheduleTimeslot.getCoolDownIntervalBeforeRepeat())) {
            scheduleTimeslot.setCoolDownIntervalBeforeRepeat(createTimeslot.getCoolDownIntervalBeforeRepeat());
            update = true;
        }

        if (createTimeslot.getStartMillisOffset() != null && !createTimeslot.getStartMillisOffset().equals(scheduleTimeslot.getStartMillisOffset())) {
            scheduleTimeslot.setStartMillisOffset(createTimeslot.getStartMillisOffset());
            update = true;
        }

        if (createTimeslot.getEndMillisOffset() != null && !createTimeslot.getEndMillisOffset().equals(scheduleTimeslot.getEndMillisOffset())) {
            scheduleTimeslot.setEndMillisOffset(createTimeslot.getEndMillisOffset());
            update = true;
        }

        if (createTimeslot.getSchedule() != null && (scheduleTimeslot.getSchedule() == null || !createTimeslot.getSchedule().getId().equals(scheduleTimeslot.getSchedule().getId()))) {
            scheduleTimeslot.setSchedule(createTimeslot.getSchedule());
            update = true;
        }

        return update;
    }


    public Optional<LocalTime> getTimeFromName(TimeOfTheDayName timeOfTheDayName, double lon, double lat) {
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

        return result.map(f -> f.toLocalTimestamp().toTemporalAccessor().toLocalTime());

    }

    private ExecuteInvokerRequest getExecuteInvokerRequest(ScheduleToAction scheduleToAction, SecurityContext securityContext) {
        Set<String> invokerNames = scheduleToAction.getRightside().getServiceCanonicalNames().parallelStream().map(f->f.getServiceCanonicalName()).collect(Collectors.toSet());
        return new ExecuteInvokerRequest()
                .setInvokerNames(invokerNames)
                .setInvokerMethodName(scheduleToAction.getRightside().getMethodName())
                .setExecutionParametersHolder(scheduleToAction.getRightside().getExecutionParametersHolder()!=null?scheduleToAction.getRightside().getExecutionParametersHolder().setSecurityContext(securityContext):null);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void runSchedule(List<ScheduleToAction> schedule, SecurityContext securityContext) {
        for (ScheduleToAction link : schedule) {

            ExecuteInvokerRequest execution = getExecuteInvokerRequest(link, securityContext);
            long start = System.currentTimeMillis();
            boolean failed = false;
            Object response = null;
            try {
                response = dynamicInvokersService.executeInvoker(execution, securityContext);
            } catch (Exception e) {
                response = e;
                failed = true;

            } finally {
                auditSchedule(securityContext, link.getRightside(), start, failed, response);
            }


        }


    }

    private void auditSchedule(SecurityContext securityContext, ScheduleAction scheduleAction, long start, boolean failed, Object response) {
        long timeTaken = System.currentTimeMillis() - start;
        SecurityContext securityContextForAuditing = getSecurityContextForAuditing(securityContext, scheduleAction);
        auditingService.addAuditingJob(new AuditingJob()
                .setDateOccured(Date.from(Instant.now()))
                .setResponse(response)
                .setTimeTaken(timeTaken)
                .setSecurityContext(securityContextForAuditing)
                .setInvocationContext(new SchduelingInvocationContext(new Object[]{scheduleAction, securityContext}))
                .setAuditingType(SchedulingAuditTypes.SCHEDULING.name())
                .setFailed(failed)
        );
    }

    private SecurityContext getSecurityContextForAuditing(SecurityContext securityContext, ScheduleAction scheduleAction) {
        SecurityContext securityContextForAuditing = new SecurityContext(securityContext.getTenants(), securityContext.getUser(), securityContext.getOperation(), securityContext.getTenantToCreateIn());
        Operation schedulingOperation = new Operation();
        schedulingOperation.setId(scheduleAction.getId());
        schedulingOperation.setName(scheduleAction.getName());
        schedulingOperation.setDescription(scheduleAction.getDescription());
        securityContextForAuditing.setOperation(schedulingOperation);
        return securityContext;
    }


    private boolean matchParameters(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return method.getParameterCount() == 2 && ScheduleAction.class.isAssignableFrom(parameterTypes[0]) && parameterTypes[1] == SecurityContext.class;
    }


    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return schedulingRepository.getByIdOrNull(id, c, batchString, securityContext);
    }


    public ScheduleToAction linkScheduleToAction(SecurityContext securityContext, LinkScheduleToAction createScheduling) {
        ScheduleToAction scheduleToAction = ScheduleToAction.s().CreateUnchecked("link", securityContext);
        scheduleToAction.Init(createScheduling.getSchedule(), createScheduling.getScheduleAction());
        schedulingRepository.merge(scheduleToAction);
        return scheduleToAction;
    }

    public List<ScheduleAction> getAllScheduleActions(SecurityContext securityContext, SchedulingActionFiltering filtering) {
        return schedulingRepository.getAllScheduleActions(filtering, securityContext);
    }

    public Schedule updateSchedule(SecurityContext securityContext, UpdateScheduling updateScheduling) {
        if (updateScheduleNoMerge(updateScheduling.getSchedule(), updateScheduling)) {
            schedulingRepository.merge(updateScheduling.getSchedule());
        }
        return updateScheduling.getSchedule();
    }

    public ScheduleAction updateScheduleAction(SecurityContext securityContext, UpdateSchedulingAction updateSchedulingAction) {
        if (updateScheduleActionNoMerge(updateSchedulingAction.getScheduleAction(), updateSchedulingAction)) {
            schedulingRepository.merge(updateSchedulingAction.getScheduleAction());
        }
        return updateSchedulingAction.getScheduleAction();
    }

    public void merge(Object o) {
        schedulingRepository.merge(o);
    }

    public List<ScheduleTimeslot> getAllTimeSlots(Set<String> scheduleIds, SecurityContext securityContext) {
        return scheduleIds.isEmpty()?new ArrayList<>():schedulingRepository.getAllTimeSlots(scheduleIds, securityContext);
    }

    public List<ScheduleTimeslot> getAllScheduleTimeslots(SecurityContext securityContext, SchedulingTimeslotFiltering filtering) {
        return schedulingRepository.getAllScheduleTimeslots(filtering, securityContext);
    }
}
