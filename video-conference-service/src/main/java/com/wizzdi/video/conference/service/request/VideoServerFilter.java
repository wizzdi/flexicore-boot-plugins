package com.wizzdi.video.conference.service.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.Set;

public class VideoServerFilter extends PaginationFilter {

  private Set<String> baseUrl;

  private BasicPropertiesFilter basicPropertiesFilter;

  public Set<String> getBaseUrl() {
    return this.baseUrl;
  }

  public <T extends VideoServerFilter> T setBaseUrl(Set<String> baseUrl) {
    this.baseUrl = baseUrl;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends VideoServerFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
