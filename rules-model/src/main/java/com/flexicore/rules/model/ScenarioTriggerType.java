package com.flexicore.rules.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;

@Entity
public class ScenarioTriggerType extends Baseclass {

  private String eventCanonicalName;

  /** @return eventCanonicalName */
  public String getEventCanonicalName() {
    return this.eventCanonicalName;
  }

  /**
   * @param eventCanonicalName eventCanonicalName to set
   * @return ScenarioTriggerType
   */
  public <T extends ScenarioTriggerType> T setEventCanonicalName(String eventCanonicalName) {
    this.eventCanonicalName = eventCanonicalName;
    return (T) this;
  }
}
