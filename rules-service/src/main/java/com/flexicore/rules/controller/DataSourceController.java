package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.model.DataSource_;
import com.flexicore.rules.request.DataSourceCreate;
import com.flexicore.rules.request.DataSourceFilter;
import com.flexicore.rules.request.DataSourceUpdate;
import com.flexicore.rules.service.DataSourceService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("DataSource")
@Extension
@Tag(name = "DataSource")
@OperationsInside
public class DataSourceController implements Plugin {

  @Autowired private DataSourceService dataSourceService;

  @PostMapping("createDataSource")
  @Operation(summary = "createDataSource", description = "Creates DataSource")
  public DataSource createDataSource(
      
      @RequestBody DataSourceCreate dataSourceCreate,
      @RequestAttribute SecurityContext securityContext) {

    dataSourceService.validate(dataSourceCreate, securityContext);
    return dataSourceService.createDataSource(dataSourceCreate, securityContext);
  }

  @PostMapping("getAllDataSources")
  @Operation(summary = "getAllDataSources", description = "lists DataSources")
  public PaginationResponse<DataSource> getAllDataSources(
      
      @RequestBody DataSourceFilter dataSourceFilter,
      @RequestAttribute SecurityContext securityContext) {

    dataSourceService.validate(dataSourceFilter, securityContext);
    return dataSourceService.getAllDataSources(dataSourceFilter, securityContext);
  }

  @PutMapping("updateDataSource")
  @Operation(summary = "updateDataSource", description = "Updates DataSource")
  public DataSource updateDataSource(
      
      @RequestBody DataSourceUpdate dataSourceUpdate,
      @RequestAttribute SecurityContext securityContext) {

    String dataSourceId = dataSourceUpdate.getId();
    DataSource dataSource =
        dataSourceService.getByIdOrNull(
            dataSourceId, DataSource.class,  securityContext);
    if (dataSource == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No DataSource with id " + dataSourceId);
    }
    dataSourceUpdate.setDataSource(dataSource);
    dataSourceService.validate(dataSourceUpdate, securityContext);
    return dataSourceService.updateDataSource(dataSourceUpdate, securityContext);
  }
}
