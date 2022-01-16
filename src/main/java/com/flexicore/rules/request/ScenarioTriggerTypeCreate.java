package com.flexicore.rules.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

/** Object Used to Create ScenarioTriggerType */
public class ScenarioTriggerTypeCreate extends BasicCreate {

  private String eventCanonicalName;

  /** @return eventCanonicalName */
  public String getEventCanonicalName() {
    return this.eventCanonicalName;
  }

  /**
   * @param eventCanonicalName eventCanonicalName to set
   * @return ScenarioTriggerTypeCreate
   */
  public <T extends ScenarioTriggerTypeCreate> T setEventCanonicalName(String eventCanonicalName) {
    this.eventCanonicalName = eventCanonicalName;
    return (T) this;
  }
}
