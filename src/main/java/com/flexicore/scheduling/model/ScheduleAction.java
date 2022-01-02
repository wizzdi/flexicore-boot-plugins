package com.flexicore.scheduling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ScheduleAction extends SecuredBasic {

  @ManyToOne(targetEntity = DynamicExecution.class)
  private DynamicExecution dynamicExecution;
  @JsonIgnore
  @OneToMany(targetEntity = ScheduleToAction.class, mappedBy = "scheduleAction")
  private List<ScheduleToAction> scheduleToActions = new ArrayList<>();

  @JsonIgnore
  @OneToMany(targetEntity = ScheduleToAction.class, mappedBy = "scheduleAction")
  public List<ScheduleToAction> getScheduleToActions() {
    return scheduleToActions;
  }

	public ScheduleAction setScheduleToActions(
			List<ScheduleToAction> scheduleToActions) {
		this.scheduleToActions = scheduleToActions;
		return this;
	}

  /** @return dynamicExecution */
  @ManyToOne(targetEntity = DynamicExecution.class)
  public DynamicExecution getDynamicExecution() {
    return this.dynamicExecution;
  }

  /**
   * @param dynamicExecution dynamicExecution to set
   * @return ScheduleAction
   */
  public <T extends ScheduleAction> T setDynamicExecution(DynamicExecution dynamicExecution) {
    this.dynamicExecution = dynamicExecution;
    return (T) this;
  }
}
