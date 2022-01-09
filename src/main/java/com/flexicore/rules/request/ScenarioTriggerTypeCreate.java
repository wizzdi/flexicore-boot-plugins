package com.flexicore.rules.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

public class ScenarioTriggerTypeCreate extends BasicCreate {

  private String eventCanonicalName;

  public String getEventCanonicalName() {
    return this.eventCanonicalName;
  }

  public <T extends ScenarioTriggerTypeCreate> T setEventCanonicalName(String eventCanonicalName) {
    this.eventCanonicalName = eventCanonicalName;
    return (T) this;
  }
}
