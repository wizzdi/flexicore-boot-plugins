package com.flexicore.scheduling.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Read;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.scheduling.containers.request.*;
import com.flexicore.scheduling.containers.response.SchedulingOperatorContainer;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleAction;
import com.flexicore.scheduling.model.ScheduleToAction;
import com.flexicore.scheduling.service.SchedulingService;
import com.flexicore.security.SecurityContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
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
	@ApiOperation(value = "getAllScheduleActions", notes = "Gets All Schedule Actions")
	@Path("getAllScheduleActions")
	public List<ScheduleAction> getAllScheduleActions(
			@HeaderParam("authenticationKey") String authenticationKey,
			SchedulingActionFiltering filtering,
			@Context SecurityContext securityContext) {

		Schedule schedule=filtering.getScheduleId()!=null?service.getByIdOrNull(filtering.getScheduleId(),Schedule.class,null,securityContext):null;
		if(schedule==null&&filtering.getScheduleId()!=null){
			throw new BadRequestException("No Schedule with id "+filtering.getScheduleId());
		}

		return service.getAllScheduleActions(securityContext, filtering);
	}


	@POST
	@Produces("application/json")
	@Read
	@ApiOperation(value = "getAvailableSchedulingOperators", notes = "Gets All Scheduling operator containers")
	@Path("getAvailableSchedulingOperators")
	public List<SchedulingOperatorContainer> getAvailableSchedulingOperators(
			@HeaderParam("authenticationKey") String authenticationKey,
			SchedulingOperatorsFiltering filtering,
			@Context SecurityContext securityContext) {

		return service.getAvailableSchedulingOperators(securityContext, filtering);
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


	@POST
	@Produces("application/json")
	@Read
	@ApiOperation(value = "createScheduleAction", notes = "Create Schedule Action")
	@Path("createScheduleAction")
	public ScheduleAction createScheduleAction(
			@HeaderParam("authenticationKey") String authenticationKey,
			CreateSchedulingAction createScheduling,
			@Context SecurityContext securityContext) {

		return service.createScheduleAction(securityContext, createScheduling);
	}


	@POST
	@Produces("application/json")
	@Read
	@ApiOperation(value = "linkScheduleToAction", notes = "Link Schedule To Action")
	@Path("linkScheduleToAction")
	public ScheduleToAction linkScheduleToAction(
			@HeaderParam("authenticationKey") String authenticationKey,
			LinkScheduleToAction createScheduling,
			@Context SecurityContext securityContext) {

		return service.linkScheduleToAction(securityContext, createScheduling);
	}


}
