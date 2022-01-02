package com.flexicore.scheduling.model;

import com.flexicore.model.SecuredBasic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ScheduleToAction extends SecuredBasic {

  @ManyToOne(targetEntity = Schedule.class)
  private Schedule schedule;

  @ManyToOne(targetEntity = ScheduleAction.class)
  private ScheduleAction scheduleAction;

  /** @return schedule */
  @ManyToOne(targetEntity = ScheduleAction.class)
  public Schedule getSchedule() {
    return this.schedule;
  }

  /**
   * @param schedule schedule to set
   * @return ScheduleToAction
   */
  public <T extends ScheduleToAction> T setSchedule(Schedule schedule) {
    this.schedule = schedule;
    return (T) this;
  }


  /** @return scheduleAction */
  @ManyToOne(targetEntity = Schedule.class)
  public ScheduleAction getScheduleAction() {
    return this.scheduleAction;
  }

  /**
   * @param scheduleAction scheduleAction to set
   * @return ScheduleToAction
   */
  public <T extends ScheduleToAction> T setScheduleAction(ScheduleAction scheduleAction) {
    this.scheduleAction = scheduleAction;
    return (T) this;
  }
}
