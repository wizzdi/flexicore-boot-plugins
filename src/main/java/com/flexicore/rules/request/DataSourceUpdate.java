package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.DataSource;

public class DataSourceUpdate extends DataSourceCreate {

  private String id;
  @JsonIgnore private DataSource dataSource;

  public String getId() {
    return id;
  }

  public <T extends DataSourceUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public DataSource getDataSource() {
    return dataSource;
  }

  public <T extends DataSourceUpdate> T setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
    return (T) this;
  }
}
