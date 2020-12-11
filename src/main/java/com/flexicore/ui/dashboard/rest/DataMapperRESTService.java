package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.DataMapper;
import com.flexicore.ui.dashboard.request.DataMapperCreate;
import com.flexicore.ui.dashboard.request.DataMapperFiltering;
import com.flexicore.ui.dashboard.request.DataMapperUpdate;
import com.flexicore.ui.dashboard.service.DataMapperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;

/**
 * Created by Asaf on 04/06/2017.
 */

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/DataMapper")
@Tag(name = "DataMapper")
@Tag(name = "Presets")
@Extension
@Component
public class DataMapperRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private DataMapperService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllDataMapper", description = "returns all DataMapper")
	@Path("getAllDataMapper")
	public PaginationResponse<DataMapper> getAllDataMapper(
			@HeaderParam("authenticationKey") String authenticationKey,
			DataMapperFiltering dataMapperFiltering,
			@Context SecurityContext securityContext) {
		service.validate(dataMapperFiltering, securityContext);
		return service.getAllDataMapper(dataMapperFiltering, securityContext);

	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateDataMapper", description = "Updates Dashbaord")
	@Path("updateDataMapper")
	public DataMapper updateDataMapper(
			@HeaderParam("authenticationKey") String authenticationKey,
			DataMapperUpdate updateDataMapper, @Context SecurityContext securityContext) {
		DataMapper dataMapper = updateDataMapper.getId() != null ? service.getByIdOrNull(
				updateDataMapper.getId(), DataMapper.class, null, securityContext) : null;
		if (dataMapper == null) {
			throw new BadRequestException("no ui field with id  "
					+ updateDataMapper.getId());
		}
		updateDataMapper.setDataMapper(dataMapper);
		service.validate(updateDataMapper, securityContext);

		return service.updateDataMapper(updateDataMapper, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createDataMapper", description = "Creates DataMapper ")
	@Path("createDataMapper")
	public DataMapper createDataMapper(
			@HeaderParam("authenticationKey") String authenticationKey,
			DataMapperCreate createDataMapper, @Context SecurityContext securityContext) {
		service.validate(createDataMapper, securityContext);
		return service.createDataMapper(createDataMapper, securityContext);

	}


}
