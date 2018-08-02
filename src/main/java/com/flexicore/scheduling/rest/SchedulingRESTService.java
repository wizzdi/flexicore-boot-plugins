package com.flexicore.scheduling.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Read;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.scheduling.containers.request.CreateScheduling;
import com.flexicore.scheduling.containers.request.SchedulingFiltering;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.service.SchedulingService;
import com.flexicore.security.SecurityContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.List;

/**
 * Created by Asaf on 04/06/2017.
 */


@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/Scheduling")

@SwaggerDefinition(tags = {
		@Tag(name="Scheduling",description = "Scheduling Services")

})
@Api(tags = {"Scheduling"})

public class SchedulingRESTService implements RestServicePlugin {

	@Inject
	@PluginInfo(version = 1)
	private SchedulingService service;



	@POST
	@Produces("application/json")
	@Read
	@ApiOperation(value = "getAllSchedules", notes = "Gets All Schedules")
	@Path("getAllSchedules")
	public List<Schedule> getAllSchedules(
			@HeaderParam("authenticationKey") String authenticationKey,
			SchedulingFiltering filtering,
			@Context SecurityContext securityContext) {

		return service.getAllSchedules(securityContext, filtering);
	}




	@POST
	@Produces("application/json")
	@Read
	@ApiOperation(value = "createSchedule", notes = "Create Schedule")
	@Path("createSchedule")
	public Schedule createSchedule(
			@HeaderParam("authenticationKey") String authenticationKey,
			CreateScheduling createScheduling,
			@Context SecurityContext securityContext) {

		return service.createSchedule(securityContext, createScheduling);
	}


}
