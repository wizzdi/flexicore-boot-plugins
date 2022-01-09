package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.rules.data.DataSourceRepository;
import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.request.DataSourceCreate;
import com.flexicore.rules.request.DataSourceFilter;
import com.flexicore.rules.request.DataSourceUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class DataSourceService implements Plugin, IDataSourceService {

  @Autowired private DataSourceRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param dataSourceCreate Object Used to Create DataSource
   * @param securityContext
   * @return created DataSource
   */
  @Override
  public DataSource createDataSource(
      DataSourceCreate dataSourceCreate, SecurityContextBase securityContext) {
    DataSource dataSource = createDataSourceNoMerge(dataSourceCreate, securityContext);
    this.repository.merge(dataSource);
    return dataSource;
  }

  /**
   * @param dataSourceCreate Object Used to Create DataSource
   * @param securityContext
   * @return created DataSource unmerged
   */
  @Override
  public DataSource createDataSourceNoMerge(
      DataSourceCreate dataSourceCreate, SecurityContextBase securityContext) {
    DataSource dataSource = new DataSource();
    dataSource.setId(UUID.randomUUID().toString());
    updateDataSourceNoMerge(dataSource, dataSourceCreate);

    BaseclassService.createSecurityObjectNoMerge(dataSource, securityContext);

    return dataSource;
  }

  /**
   * @param dataSourceCreate Object Used to Create DataSource
   * @param dataSource
   * @return if dataSource was updated
   */
  @Override
  public boolean updateDataSourceNoMerge(DataSource dataSource, DataSourceCreate dataSourceCreate) {
    boolean update = basicService.updateBasicNoMerge(dataSourceCreate, dataSource);

    if (dataSourceCreate.getDynamicExecution() != null
        && (dataSource.getDynamicExecution() == null
            || !dataSourceCreate
                .getDynamicExecution()
                .getId()
                .equals(dataSource.getDynamicExecution().getId()))) {
      dataSource.setDynamicExecution(dataSourceCreate.getDynamicExecution());
      update = true;
    }

    return update;
  }
  /**
   * @param dataSourceUpdate
   * @param securityContext
   * @return dataSource
   */
  @Override
  public DataSource updateDataSource(
      DataSourceUpdate dataSourceUpdate, SecurityContextBase securityContext) {
    DataSource dataSource = dataSourceUpdate.getDataSource();
    if (updateDataSourceNoMerge(dataSource, dataSourceUpdate)) {
      this.repository.merge(dataSource);
    }
    return dataSource;
  }

  /**
   * @param dataSourceFilter Object Used to List DataSource
   * @param securityContext
   * @return PaginationResponse containing paging information for DataSource
   */
  @Override
  public PaginationResponse<DataSource> getAllDataSources(
      DataSourceFilter dataSourceFilter, SecurityContextBase securityContext) {
    List<DataSource> list = listAllDataSources(dataSourceFilter, securityContext);
    long count = this.repository.countAllDataSources(dataSourceFilter, securityContext);
    return new PaginationResponse<>(list, dataSourceFilter, count);
  }

  /**
   * @param dataSourceFilter Object Used to List DataSource
   * @param securityContext
   * @return List of DataSource
   */
  @Override
  public List<DataSource> listAllDataSources(
      DataSourceFilter dataSourceFilter, SecurityContextBase securityContext) {
    return this.repository.listAllDataSources(dataSourceFilter, securityContext);
  }

  /**
   * @param dataSourceFilter Object Used to List DataSource
   * @param securityContext
   * @throws ResponseStatusException if dataSourceFilter is not valid
   */
  @Override
  public void validate(DataSourceFilter dataSourceFilter, SecurityContextBase securityContext) {
    basicService.validate(dataSourceFilter, securityContext);

    Set<String> dynamicExecutionIds =
        dataSourceFilter.getDynamicExecutionIds() == null
            ? new HashSet<>()
            : dataSourceFilter.getDynamicExecutionIds();
    Map<String, DynamicExecution> dynamicExecution =
        dynamicExecutionIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(
                    DynamicExecution.class,
                    dynamicExecutionIds,
                    SecuredBasic_.security,
                    securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    dynamicExecutionIds.removeAll(dynamicExecution.keySet());
    if (!dynamicExecutionIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No DynamicExecution with ids " + dynamicExecutionIds);
    }
    dataSourceFilter.setDynamicExecution(new ArrayList<>(dynamicExecution.values()));
  }

  /**
   * @param dataSourceCreate Object Used to Create DataSource
   * @param securityContext
   * @throws ResponseStatusException if dataSourceCreate is not valid
   */
  @Override
  public void validate(DataSourceCreate dataSourceCreate, SecurityContextBase securityContext) {
    basicService.validate(dataSourceCreate, securityContext);

    String dynamicExecutionId = dataSourceCreate.getDynamicExecutionId();
    DynamicExecution dynamicExecution =
        dynamicExecutionId == null
            ? null
            : this.repository.getByIdOrNull(
                dynamicExecutionId,
                DynamicExecution.class,
                SecuredBasic_.security,
                securityContext);
    if (dynamicExecutionId != null && dynamicExecution == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No DynamicExecution with id " + dynamicExecutionId);
    }
    dataSourceCreate.setDynamicExecution(dynamicExecution);
  }

  @Override
  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, securityContext);
  }

  @Override
  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return this.repository.findByIds(c, ids, idAttribute);
  }

  @Override
  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return this.repository.findByIds(c, requested);
  }

  @Override
  public <T> T findByIdOrNull(Class<T> type, String id) {
    return this.repository.findByIdOrNull(type, id);
  }

  @Override
  public void merge(java.lang.Object base) {
    this.repository.merge(base);
  }

  @Override
  public void massMerge(List<?> toMerge) {
    this.repository.massMerge(toMerge);
  }
}
