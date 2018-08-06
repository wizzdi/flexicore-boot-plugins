package com.flexicore.scheduling.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Read;
import com.flexicore.interfaces.InitPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.User;
import com.flexicore.scheduling.containers.request.CreateScheduling;
import com.flexicore.scheduling.containers.request.CreateSchedulingAction;
import com.flexicore.scheduling.containers.request.SchedulingFiltering;
import com.flexicore.scheduling.containers.request.SchedulingOperatorsFiltering;
import com.flexicore.scheduling.containers.response.SchedulingMethod;
import com.flexicore.scheduling.containers.response.SchedulingOperatorContainer;
import com.flexicore.scheduling.data.SchedulingRepository;
import com.flexicore.scheduling.interfaces.SchedulingOperator;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleAction;
import com.flexicore.security.RunningUser;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.PluginService;
import com.flexicore.service.SecurityService;
import com.flexicore.service.UserService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        User user = userService.getUserByMail("admin@flexicore.com");
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
        scheduling.setDescription(createScheduling.getDescription());
        scheduling.setSunday(createScheduling.isSunday());
        scheduling.setMonday(createScheduling.isMonday());
        scheduling.setTuesday(createScheduling.isTuesday());
        scheduling.setWednesday(createScheduling.isWednesday());
        scheduling.setThursday(createScheduling.isThursday());
        scheduling.setFriday(createScheduling.isFriday());
        scheduling.setSaturday(createScheduling.isSaturday());
        scheduling.setMillisOffset(createScheduling.getMillisOffset());
        scheduling.setTimeFrameStart(createScheduling.getTimeFrameStart());
        scheduling.setTimeFrameEnd(createScheduling.getTimeFrameEnd());
        scheduling.setTimeOfTheDay(createScheduling.getTimeOfTheDay());
        scheduling.setTimeOfTheDayName(createScheduling.getTimeOfTheDayName());
        for (CreateSchedulingAction createSchedulingAction : createScheduling.getActions()) {
            ScheduleAction scheduleAction = ScheduleAction.s().CreateUnchecked(createSchedulingAction.getMethodName(), securityContext.getUser());
            scheduleAction.Init();
            scheduleAction.setMethodName(createSchedulingAction.getMethodName());
            scheduleAction.setServiceCanonicalName(createSchedulingAction.getServiceCanonicalName());
            scheduleAction.setParameter1(createSchedulingAction.getParameter1());
            scheduleAction.setParameter2(createSchedulingAction.getParameter2());
            scheduleAction.setParameter3(createSchedulingAction.getParameter3());
            scheduleAction.setSchedule(scheduling);

        }
        return scheduling;

    }

    public List<Schedule> getAllSchedules(SecurityContext securityContext, SchedulingFiltering filtering) {
        QueryInformationHolder<Schedule> queryInformationHolder = new QueryInformationHolder<>(filtering, Schedule.class, securityContext);
        return schedulingRepository.getAllFiltered(queryInformationHolder);
    }

    public LocalDateTime getTimeFromName(String timeOfTheDayName) {
        return null;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void runSchedule(Schedule schedule, SecurityContext securityContext) {
        if (schedule.getScheduleActionList().isEmpty()) {
            return;
        }
        List<SchedulingOperator> list = (List<SchedulingOperator>) pluginService.getPlugins(SchedulingOperator.class, new HashMap<>(), null);
        Map<String, SchedulingOperator> schedulingOperatorMap = list.parallelStream().collect(Collectors.toMap(f -> f.getClass().getCanonicalName(), f -> f));
        for (ScheduleAction scheduleAction : schedule.getScheduleActionList()) {
            SchedulingOperator schedulingOperator = schedulingOperatorMap.get(scheduleAction.getServiceCanonicalName());
            if (schedulingOperator != null) {
                executeAction(schedulingOperator, scheduleAction, securityContext);
            }
        }
    }

    private void executeAction(SchedulingOperator schedulingOperator, ScheduleAction scheduleAction, SecurityContext securityContext) {
        try {
            Method m = methodCache.get(scheduleAction.getMethodName(), () -> getMethod(schedulingOperator, scheduleAction));
            m.invoke(schedulingOperator, scheduleAction, securityContext);
        } catch (ExecutionException e) {
            logger.log(Level.SEVERE, "unable to get method for " + scheduleAction, e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.SEVERE, "unable to execute method for " + scheduleAction, e);

        }
    }

    private Method getMethod(SchedulingOperator schedulingOperator, ScheduleAction scheduleAction) throws NoSuchMethodException {
        return schedulingOperator.getClass().getDeclaredMethod(scheduleAction.getMethodName(), ScheduleAction.class, SecurityContext.class);
    }

    public List<SchedulingOperatorContainer> getAvailableSchedulingOperators(SecurityContext securityContext, SchedulingOperatorsFiltering filtering) {
        List<SchedulingOperator> list = (List<SchedulingOperator>) pluginService.getPlugins(SchedulingOperator.class, new HashMap<>(), null);
        return list.parallelStream().map(f->getSchedulingOperatorContainer(f)).collect(Collectors.toList());
    }

    private SchedulingOperatorContainer getSchedulingOperatorContainer(SchedulingOperator f) {
        List<SchedulingMethod> list=new ArrayList<>();
        Class<? extends SchedulingOperator> clazz = f.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if(matchParameters(method)){
                list.add(new SchedulingMethod(method.getName(),null));
            }
        }
        return new SchedulingOperatorContainer(clazz.getCanonicalName(),list);
    }

    private boolean matchParameters(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return method.getParameterCount()==2 && ScheduleAction.class.isAssignableFrom(parameterTypes[0]) && parameterTypes[1]==SecurityContext.class;
    }
}
