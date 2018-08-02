package com.flexicore.scheduling.model;

import com.flexicore.model.Baseclass;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class ScheduleAction extends Baseclass {
    private static ScheduleAction s_Singleton=new ScheduleAction();
    public  static ScheduleAction s() {return s_Singleton;}


    @ManyToOne(targetEntity = Schedule.class)
    private Schedule schedule;
    private String serviceCanonicalName;
    private String methodName;
    @Lob
    private String parameter1;
    @Lob
    private String parameter2;
    @Lob
    private String parameter3;

    @ManyToOne(targetEntity = Schedule.class)
    public Schedule getSchedule() {
        return schedule;
    }

    public ScheduleAction setSchedule(Schedule schedule) {
        this.schedule = schedule;
        return this;
    }


    public String getServiceCanonicalName() {
        return serviceCanonicalName;
    }

    public ScheduleAction setServiceCanonicalName(String serviceCanonicalName) {
        this.serviceCanonicalName = serviceCanonicalName;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public ScheduleAction setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }
    @Lob
    public String getParameter1() {
        return parameter1;
    }

    public ScheduleAction setParameter1(String parameter1) {
        this.parameter1 = parameter1;
        return this;
    }
    @Lob
    public String getParameter2() {
        return parameter2;
    }

    public ScheduleAction setParameter2(String parameter2) {
        this.parameter2 = parameter2;
        return this;
    }
    @Lob
    public String getParameter3() {
        return parameter3;
    }

    public ScheduleAction setParameter3(String parameter3) {
        this.parameter3 = parameter3;
        return this;
    }
}
