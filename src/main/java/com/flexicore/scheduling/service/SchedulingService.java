package com.flexicore.scheduling.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Read;
import com.flexicore.interfaces.InitPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Operation;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.User;
import com.flexicore.model.auditing.AuditingJob;
import com.flexicore.scheduling.containers.request.*;
import com.flexicore.scheduling.containers.response.SchedulingMethod;
import com.flexicore.scheduling.containers.response.SchedulingOperatorContainer;
import com.flexicore.scheduling.data.SchedulingRepository;
import com.flexicore.scheduling.init.Config;
import com.flexicore.scheduling.interfaces.SchedulingOperator;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleAction;
import com.flexicore.scheduling.model.ScheduleToAction;
import com.flexicore.security.RunningUser;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.AuditingService;
import com.flexicore.service.PluginService;
import com.flexicore.service.SecurityService;
import com.flexicore.service.UserService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.calendar.astro.SolarTime;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
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
        User user = userService.getUserByMail(Config.scheduleUserEmail);
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
        Schedule scheduling = Schedule.s().CreateUnchecked(createScheduling.getName(), securityContext.getUser());
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
        if (createScheduling.getMillisOffset() != null && !createScheduling.getMillisOffset().equals(schedule.getMillisOffset())) {
            schedule.setMillisOffset(createScheduling.getMillisOffset());
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

        if (createScheduling.getTimeOfTheDayStart() != null && !createScheduling.getTimeOfTheDayStart().equals(schedule.getTimeOfTheDayStart())) {
            schedule.setTimeOfTheDayStart(createScheduling.getTimeOfTheDayStart());
            update = true;
        }

        if (createScheduling.getTimeOfTheDayEnd() != null && !createScheduling.getTimeOfTheDayEnd().equals(schedule.getTimeOfTheDayEnd())) {
            schedule.setTimeOfTheDayEnd(createScheduling.getTimeOfTheDayEnd());
            update = true;
        }

        if (createScheduling.getCoolDownIntervalBeforeRepeat() != null && !createScheduling.getCoolDownIntervalBeforeRepeat().equals(schedule.getCoolDownIntervalBeforeRepeat())) {
            schedule.setCoolDownIntervalBeforeRepeat(createScheduling.getCoolDownIntervalBeforeRepeat());
            update = true;
        }

        if (createScheduling.getTimeOfTheDayName() != null && !createScheduling.getTimeOfTheDayName().equals(schedule.getTimeOfTheDayName())) {
            schedule.setTimeOfTheDayName(createScheduling.getTimeOfTheDayName());
            update = true;
        }

        if (createScheduling.getTimeOfTheDayNameLat() != null && !createScheduling.getTimeOfTheDayNameLat().equals(schedule.getTimeOfTheDayNameLat())) {
            schedule.setTimeOfTheDayNameLat(createScheduling.getTimeOfTheDayNameLat());
            update = true;
        }

        if (createScheduling.getTimeOfTheDayNameLon() != null && !createScheduling.getTimeOfTheDayNameLon().equals(schedule.getTimeOfTheDayNameLon())) {
            schedule.setTimeOfTheDayNameLon(createScheduling.getTimeOfTheDayNameLon());
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
        return update;
    }

    public ScheduleAction createScheduleAction(SecurityContext securityContext, CreateSchedulingAction createSchedulingAction) {
        ScheduleAction scheduleAction = createScheduleActionNoMerge(securityContext, createSchedulingAction);
        schedulingRepository.merge(scheduleAction);
        return scheduleAction;
    }

    public ScheduleAction createScheduleActionNoMerge(SecurityContext securityContext, CreateSchedulingAction createSchedulingAction) {
        ScheduleAction scheduleAction = ScheduleAction.s().CreateUnchecked(createSchedulingAction.getName(), securityContext.getUser());
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
        if (createSchedulingAction.getParameter1() != null && !createSchedulingAction.getParameter1().equals(scheduleAction.getParameter1())) {
            scheduleAction.setParameter1(createSchedulingAction.getParameter1());
            update = true;
        }
        if (createSchedulingAction.getParameter2() != null && !createSchedulingAction.getParameter2().equals(scheduleAction.getParameter2())) {
            scheduleAction.setParameter2(createSchedulingAction.getParameter2());
            update = true;
        }

        if (createSchedulingAction.getParameter3() != null && !createSchedulingAction.getParameter3().equals(scheduleAction.getParameter3())) {
            scheduleAction.setParameter3(createSchedulingAction.getParameter3());
            update = true;
        }

        if (createSchedulingAction.getServiceCanonicalName() != null && !createSchedulingAction.getServiceCanonicalName().equals(scheduleAction.getServiceCanonicalName())) {
            scheduleAction.setServiceCanonicalName(createSchedulingAction.getServiceCanonicalName());
            update = true;
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

    enum TimeOfTheDayName {
        SUNRISE, SUNSET;
    }

    public Optional<LocalTime> getTimeFromName(Schedule schedule) {
        SolarTime solarTime;
        Optional<Moment> result = Optional.empty();
        switch (schedule.getTimeOfTheDayName()) {
            case SUNRISE:
                solarTime = SolarTime.ofLocation(schedule.getTimeOfTheDayNameLat(), schedule.getTimeOfTheDayNameLon());
                result = PlainDate.nowInSystemTime().get(solarTime.sunrise());
                break;
            case SUNSET:
                solarTime = SolarTime.ofLocation(schedule.getTimeOfTheDayNameLat(), schedule.getTimeOfTheDayNameLon());
                result = PlainDate.nowInSystemTime().get(solarTime.sunset());
                break;
        }

        return result.map(f -> f.toLocalTimestamp().toTemporalAccessor().toLocalTime());

    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void runSchedule(List<ScheduleToAction> schedule, SecurityContext securityContext) {

        List<SchedulingOperator> list = (List<SchedulingOperator>) pluginService.getPlugins(SchedulingOperator.class, new HashMap<>(), null);
        Map<String, SchedulingOperator> schedulingOperatorMap = list.parallelStream().collect(Collectors.toMap(f -> f.getClass().getCanonicalName(), f -> f));
        for (ScheduleToAction link : schedule) {

            ScheduleAction scheduleAction = link.getRightside();
            SchedulingOperator schedulingOperator = schedulingOperatorMap.get(scheduleAction.getServiceCanonicalName());
            if (schedulingOperator != null) {
                long start = System.currentTimeMillis();
                boolean failed = false;
                Object response = null;
                try {
                    response = executeAction(schedulingOperator, scheduleAction, securityContext);
                } catch (Exception e) {
                    response = e;
                    failed = true;

                } finally {
                    auditSchedule(securityContext, scheduleAction, start, failed, response);
                }

            }
        }
        for (SchedulingOperator schedulingOperator : list) {
            pluginService.cleanUpInstance(schedulingOperator);
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

    private Object executeAction(SchedulingOperator schedulingOperator, ScheduleAction scheduleAction, SecurityContext securityContext) {
        try {
            Method m = methodCache.get(scheduleAction.getMethodName(), () -> getMethod(schedulingOperator, scheduleAction));
            return m.invoke(schedulingOperator, scheduleAction, securityContext);
        } catch (ExecutionException e) {
            logger.log(Level.SEVERE, "unable to get method for " + scheduleAction, e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.SEVERE, "unable to execute method for " + scheduleAction, e);

        }
        return null;
    }

    private Method getMethod(SchedulingOperator schedulingOperator, ScheduleAction scheduleAction) throws NoSuchMethodException {
        return schedulingOperator.getClass().getDeclaredMethod(scheduleAction.getMethodName(), ScheduleAction.class, SecurityContext.class);
    }

    public List<SchedulingOperatorContainer> getAvailableSchedulingOperators(SecurityContext securityContext, SchedulingOperatorsFiltering filtering) {
        List<SchedulingOperator> list = (List<SchedulingOperator>) pluginService.getPlugins(SchedulingOperator.class, new HashMap<>(), null);
        List<SchedulingOperatorContainer> toRet = list.parallelStream().map(f -> getSchedulingOperatorContainer(f)).collect(Collectors.toList());
        for (SchedulingOperator schedulingOperator : list) {
            pluginService.cleanUpInstance(schedulingOperator);
        }
        return toRet;
    }

    private SchedulingOperatorContainer getSchedulingOperatorContainer(SchedulingOperator f) {
        List<SchedulingMethod> list = new ArrayList<>();
        Class<? extends SchedulingOperator> clazz = f.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (matchParameters(method)) {
                com.flexicore.scheduling.interfaces.SchedulingMethod schedulingMethod = method.getDeclaredAnnotation(com.flexicore.scheduling.interfaces.SchedulingMethod.class);

                list.add(new SchedulingMethod(method.getName(), schedulingMethod));
            }
        }
        return new SchedulingOperatorContainer(clazz.getCanonicalName(), list);
    }

    private boolean matchParameters(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return method.getParameterCount() == 2 && ScheduleAction.class.isAssignableFrom(parameterTypes[0]) && parameterTypes[1] == SecurityContext.class;
    }


    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return schedulingRepository.getByIdOrNull(id, c, batchString, securityContext);
    }


    public ScheduleToAction linkScheduleToAction(SecurityContext securityContext, LinkScheduleToAction createScheduling) {
        ScheduleToAction scheduleToAction = ScheduleToAction.s().CreateUnchecked("link", securityContext.getUser());
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
}
