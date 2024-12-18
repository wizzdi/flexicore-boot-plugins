package com.flexicore.scheduling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ScheduleAction extends Baseclass {

  @ManyToOne(targetEntity = DynamicExecution.class)
  private DynamicExecution dynamicExecution;
  @JsonIgnore
  @OneToMany(targetEntity = ScheduleToAction.class, mappedBy = "scheduleAction")
  private List<ScheduleToAction> scheduleToActions = new ArrayList<>();
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime lastExecution;

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

    public OffsetDateTime getLastExecution() {
        return lastExecution;
    }

    public <T extends ScheduleAction> T setLastExecution(OffsetDateTime lastExecution) {
        this.lastExecution = lastExecution;
        return (T) this;
    }
}
