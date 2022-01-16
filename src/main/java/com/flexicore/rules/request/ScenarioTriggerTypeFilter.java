package com.flexicore.rules.request;

import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.Set;

/** Object Used to List ScenarioTriggerType */
public class ScenarioTriggerTypeFilter extends PaginationFilter {

  private Set<String> eventCanonicalName;

  /** @return eventCanonicalName */
  public Set<String> getEventCanonicalName() {
    return this.eventCanonicalName;
  }

  /**
   * @param eventCanonicalName eventCanonicalName to set
   * @return ScenarioTriggerTypeFilter
   */
  public <T extends ScenarioTriggerTypeFilter> T setEventCanonicalName(
      Set<String> eventCanonicalName) {
    this.eventCanonicalName = eventCanonicalName;
    return (T) this;
  }
}
