package com.wizzdi.flexicore.building.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.Set;

public class RoomFilter extends PaginationFilter {

  private Set<String> externalId;

  private BasicPropertiesFilter basicPropertiesFilter;

  public Set<String> getExternalId() {
    return this.externalId;
  }

  public <T extends RoomFilter> T setExternalId(Set<String> externalId) {
    this.externalId = externalId;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends RoomFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
