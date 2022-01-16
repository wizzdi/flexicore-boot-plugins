package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.DataSource;

/** Object Used to Update DataSource */
public class DataSourceUpdate extends DataSourceCreate {

  private String id;

  @JsonIgnore private DataSource dataSource;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return DataSourceUpdate
   */
  public <T extends DataSourceUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return dataSource */
  @JsonIgnore
  public DataSource getDataSource() {
    return this.dataSource;
  }

  /**
   * @param dataSource dataSource to set
   * @return DataSourceUpdate
   */
  public <T extends DataSourceUpdate> T setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
    return (T) this;
  }
}
