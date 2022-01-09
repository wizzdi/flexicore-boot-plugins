package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.request.DataSourceCreate;
import com.flexicore.rules.request.DataSourceFilter;
import com.flexicore.rules.request.DataSourceUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IDataSourceService {

  /**
   * @param dataSourceCreate Object Used to Create DataSource
   * @param securityContext
   * @return created DataSource
   */
  DataSource createDataSource(
      DataSourceCreate dataSourceCreate, SecurityContextBase securityContext);

  /**
   * @param dataSourceCreate Object Used to Create DataSource
   * @param securityContext
   * @return created DataSource unmerged
   */
  DataSource createDataSourceNoMerge(
      DataSourceCreate dataSourceCreate, SecurityContextBase securityContext);

  /**
   * @param dataSourceCreate Object Used to Create DataSource
   * @param dataSource
   * @return if dataSource was updated
   */
  boolean updateDataSourceNoMerge(DataSource dataSource, DataSourceCreate dataSourceCreate);

  /**
   * @param dataSourceUpdate
   * @param securityContext
   * @return dataSource
   */
  DataSource updateDataSource(
      DataSourceUpdate dataSourceUpdate, SecurityContextBase securityContext);

  /**
   * @param dataSourceFilter Object Used to List DataSource
   * @param securityContext
   * @return PaginationResponse containing paging information for DataSource
   */
  PaginationResponse<DataSource> getAllDataSources(
      DataSourceFilter dataSourceFilter, SecurityContextBase securityContext);

  /**
   * @param dataSourceFilter Object Used to List DataSource
   * @param securityContext
   * @return List of DataSource
   */
  List<DataSource> listAllDataSources(
      DataSourceFilter dataSourceFilter, SecurityContextBase securityContext);

  /**
   * @param dataSourceFilter Object Used to List DataSource
   * @param securityContext
   * @throws ResponseStatusException if dataSourceFilter is not valid
   */
  void validate(DataSourceFilter dataSourceFilter, SecurityContextBase securityContext);

  /**
   * @param dataSourceCreate Object Used to Create DataSource
   * @param securityContext
   * @throws ResponseStatusException if dataSourceCreate is not valid
   */
  void validate(DataSourceCreate dataSourceCreate, SecurityContextBase securityContext);

  <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext);

  <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext);

  <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext);

  <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext);

  <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute);

  <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested);

  <T> T findByIdOrNull(Class<T> type, String id);

  void merge(java.lang.Object base);

  void massMerge(List<?> toMerge);
}
