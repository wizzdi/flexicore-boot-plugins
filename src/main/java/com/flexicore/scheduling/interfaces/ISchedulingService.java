package com.flexicore.scheduling.interfaces;

import com.flexicore.interfaces.InitPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.scheduling.containers.request.CreateScheduling;
import com.flexicore.scheduling.containers.request.CreateSchedulingAction;
import com.flexicore.scheduling.containers.request.CreateTimeslot;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleAction;
import com.flexicore.scheduling.model.ScheduleTimeslot;
import com.flexicore.security.SecurityContext;

import java.util.List;

public interface ISchedulingService extends ServicePlugin, InitPlugin {
    Schedule createScheduleNoMerge(SecurityContext securityContext, CreateScheduling createScheduling);

    ScheduleAction createScheduleActionNoMerge(SecurityContext securityContext, CreateSchedulingAction createSchedulingAction);

    ScheduleTimeslot createScheduleTimeSlotNoMerge(SecurityContext securityContext, CreateTimeslot createTimeslot);

    void massMerge(List<?> toMerge);
}
