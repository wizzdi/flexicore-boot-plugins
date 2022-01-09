package com.flexicore.rules.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.Set;

public class ScenarioTriggerTypeFilter extends PaginationFilter {

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<String> eventCanonicalName;

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends ScenarioTriggerTypeFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  public Set<String> getEventCanonicalName() {
    return this.eventCanonicalName;
  }

  public <T extends ScenarioTriggerTypeFilter> T setEventCanonicalName(
      Set<String> eventCanonicalName) {
    this.eventCanonicalName = eventCanonicalName;
    return (T) this;
  }
}
