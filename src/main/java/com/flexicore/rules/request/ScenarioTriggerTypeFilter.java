package com.flexicore.rules.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.Set;

/** Object Used to List ScenarioTriggerType */
public class ScenarioTriggerTypeFilter extends PaginationFilter {

  private Set<String> eventCanonicalName;

  private BasicPropertiesFilter basicPropertiesFilter;

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

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return ScenarioTriggerTypeFilter
   */
  public <T extends ScenarioTriggerTypeFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
