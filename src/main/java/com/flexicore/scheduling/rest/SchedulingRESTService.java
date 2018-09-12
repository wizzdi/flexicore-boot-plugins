package com.flexicore.scheduling.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Read;
import com.flexicore.annotations.rest.Update;
import com.flexicore.annotations.rest.Write;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.scheduling.containers.request.*;
import com.flexicore.scheduling.containers.response.SchedulingOperatorContainer;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleAction;
import com.flexicore.scheduling.model.ScheduleTimeslot;
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
		filtering.setSchedule(schedule);

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
	@Write
	@ApiOperation(value = "createScheduleTimeSlot", notes = "Create Schedule time slot")
	@Path("createScheduleTimeslot")
	public ScheduleTimeslot createScheduleTimeSlot(
			@HeaderParam("authenticationKey") String authenticationKey,
			CreateTimeslot createTimeslot,
			@Context SecurityContext securityContext) {
		Schedule schedule=createTimeslot.getScheduleId()!=null?service.getByIdOrNull(createTimeslot.getScheduleId(),Schedule.class,null,securityContext):null;
		if(schedule==null){
			throw new BadRequestException("No Schedule with id "+createTimeslot.getScheduleId());
		}
		createTimeslot.setSchedule(schedule);

		return service.createScheduleTimeSlot(securityContext, createTimeslot);
	}


	@PUT
	@Produces("application/json")
	@Update
	@ApiOperation(value = "updateScheduleTimeSlot", notes = "updates Schedule time slot")
	@Path("updateScheduleTimeSlot")
	public ScheduleTimeslot updateScheduleTimeSlot(
			@HeaderParam("authenticationKey") String authenticationKey,
			UpdateTimeslot updateTimeslot,
			@Context SecurityContext securityContext) {
		ScheduleTimeslot scheduleTimeslot=updateTimeslot.getScheduleTimeslotId()!=null?service.getByIdOrNull(updateTimeslot.getScheduleTimeslotId(),ScheduleTimeslot.class,null,securityContext):null;
		if(scheduleTimeslot==null){
			throw new BadRequestException("No timeslot with id "+updateTimeslot.getScheduleTimeslotId());
		}
		updateTimeslot.setScheduleTimeslot(scheduleTimeslot);

		return service.createScheduleTimeSlot(securityContext, updateTimeslot);
	}

	@POST
	@Produces("application/json")
	@Read
	@ApiOperation(value = "updateSchedule", notes = "Update Schedule")
	@Path("updateSchedule")
	public Schedule updateSchedule(
			@HeaderParam("authenticationKey") String authenticationKey,
			UpdateScheduling updateScheduling,
			@Context SecurityContext securityContext) {

		Schedule schedule=updateScheduling.getScheduleId()!=null?service.getByIdOrNull(updateScheduling.getScheduleId(),Schedule.class,null,securityContext):null;
		if(schedule==null&&updateScheduling.getScheduleId()!=null){
			throw new BadRequestException("No Schedule with id "+updateScheduling.getScheduleId());
		}
		updateScheduling.setSchedule(schedule);

		return service.updateSchedule(securityContext, updateScheduling);
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
	@ApiOperation(value = "updateScheduleAction", notes = "Update Schedule Action")
	@Path("updateScheduleAction")
	public ScheduleAction updateScheduleAction(
			@HeaderParam("authenticationKey") String authenticationKey,
			UpdateSchedulingAction updateSchedulingAction,
			@Context SecurityContext securityContext) {

		ScheduleAction action=updateSchedulingAction.getScheduleActionId()!=null?service.getByIdOrNull(updateSchedulingAction.getScheduleActionId(),ScheduleAction.class,null,securityContext):null;
		if(action==null&&updateSchedulingAction.getScheduleActionId()!=null){
			throw new BadRequestException("No action with id "+updateSchedulingAction.getScheduleActionId());
		}
		updateSchedulingAction.setScheduleAction(action);

		return service.updateScheduleAction(securityContext, updateSchedulingAction);
	}





	@POST
	@Produces("application/json")
	@Read
	@ApiOperation(value = "linkScheduleToAction", notes = "Link Schedule To Action")
	@Path("linkScheduleToAction")
	public ScheduleToAction linkScheduleToAction(
			@HeaderParam("authenticationKey") String authenticationKey,
			LinkScheduleToAction linkScheduleToAction,
			@Context SecurityContext securityContext) {

		verifyLinkContainer(linkScheduleToAction, securityContext);


		return service.linkScheduleToAction(securityContext, linkScheduleToAction);
	}


	@POST
	@Produces("application/json")
	@Read
	@ApiOperation(value = "unlinkScheduleToAction", notes = "Unlinks Schedule To Action")
	@Path("unlinkScheduleToAction")
	public ScheduleToAction unlinkScheduleToAction(
			@HeaderParam("authenticationKey") String authenticationKey,
			LinkScheduleToAction linkScheduleToAction,
			@Context SecurityContext securityContext) {

		verifyLinkContainer(linkScheduleToAction, securityContext);


		return service.unlinkScheduleToAction(securityContext, linkScheduleToAction);
	}

	private void verifyLinkContainer(LinkScheduleToAction linkScheduleToAction, @Context SecurityContext securityContext) {
		Schedule schedule=linkScheduleToAction.getScheduleId()!=null?service.getByIdOrNull(linkScheduleToAction.getScheduleId(),Schedule.class,null,securityContext):null;
		if(schedule==null&&linkScheduleToAction.getScheduleId()!=null){
			throw new BadRequestException("No Schedule with id "+linkScheduleToAction.getScheduleId());
		}
		linkScheduleToAction.setSchedule(schedule);

		ScheduleAction action=linkScheduleToAction.getActionId()!=null?service.getByIdOrNull(linkScheduleToAction.getActionId(),ScheduleAction.class,null,securityContext):null;
		if(action==null&&linkScheduleToAction.getActionId()!=null){
			throw new BadRequestException("No action with id "+linkScheduleToAction.getActionId());
		}
		linkScheduleToAction.setScheduleAction(action);
	}


}
