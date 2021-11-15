package com.wizzdi.video.conference.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.video.conference.model.VideoServer;
import java.util.List;
import java.util.Set;

public class RoomFilter extends PaginationFilter {

  private Set<String> videoServerIds;

  @JsonIgnore private List<VideoServer> videoServer;

  private BasicPropertiesFilter basicPropertiesFilter;

  public Set<String> getVideoServerIds() {
    return this.videoServerIds;
  }

  public <T extends RoomFilter> T setVideoServerIds(Set<String> videoServerIds) {
    this.videoServerIds = videoServerIds;
    return (T) this;
  }

  @JsonIgnore
  public List<VideoServer> getVideoServer() {
    return this.videoServer;
  }

  public <T extends RoomFilter> T setVideoServer(List<VideoServer> videoServer) {
    this.videoServer = videoServer;
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
