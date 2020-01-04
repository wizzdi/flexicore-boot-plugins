package com.flexicore.territories.rest;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.annotations.ProtectedREST;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.OperationsInside;

import javax.interceptor.Interceptors;

import com.flexicore.interfaces.RestServicePlugin;

import javax.ws.rs.Path;

import com.flexicore.territories.service.CityService;

import javax.inject.Inject;

import com.flexicore.security.SecurityContext;

import java.util.List;

import com.flexicore.territories.request.CityFiltering;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.HeaderParam;

import io.swagger.v3.oas.annotations.Operation;
import com.flexicore.annotations.IOperation;

import javax.ws.rs.core.Context;

import com.flexicore.model.territories.City;
import com.flexicore.territories.request.CityUpdateContainer;

import javax.ws.rs.BadRequestException;

import com.flexicore.territories.request.CityCreationContainer;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/City")
@Tag(name = "City")

public class CityRESTService implements RestServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private CityService service;

    @POST
    @Produces("application/json")
    @Operation(summary = "listAllCities", description = "Lists all Cities Filtered")
    @IOperation(Name = "listAllCities", Description = "Lists all Cities Filtered")
    @Path("listAllCities")
    public List<City> listAllCities(
            @HeaderParam("authenticationKey") String authenticationKey,
            CityFiltering filtering, @Context SecurityContext securityContext) {
        service.validate(filtering,securityContext);
        return service.listAllCities(securityContext, filtering);
    }


    @POST
    @Produces("application/json")
    @Operation(summary = "getAllCities", description = "Lists all Cities Filtered")
    @IOperation(Name = "getAllCities", Description = "Lists all Cities Filtered")
    @Path("getAllCities")
    public PaginationResponse<City> getAllCities(
            @HeaderParam("authenticationKey") String authenticationKey,
            CityFiltering filtering, @Context SecurityContext securityContext) {
        service.validate(filtering,securityContext);
        return service.getAllCities(securityContext, filtering);
    }

    @POST
    @Produces("application/json")
    @Path("/updateCity")
    @Operation(summary = "updateCity", description = "Updates City")
    @IOperation(Name = "updateCity", Description = "Updates City")
    public City updateCity(
            @HeaderParam("authenticationKey") String authenticationKey,
            CityUpdateContainer updateContainer,
            @Context SecurityContext securityContext) {
        City city = service.getByIdOrNull(updateContainer.getId(), City.class, null, securityContext);
        if (city == null) {
            throw new BadRequestException("no City with id " + updateContainer.getId());
        }
        updateContainer.setCity(city);
        service.validate(updateContainer, securityContext);
        return service.updateCity(updateContainer, securityContext);
    }

    @POST
    @Produces("application/json")
    @Path("/createCity")
    @Operation(summary = "createCity", description = "Creates City")
    @IOperation(Name = "createCity", Description = "Creates City")
    public City createCity(
            @HeaderParam("authenticationKey") String authenticationKey,
            CityCreationContainer creationContainer,
            @Context SecurityContext securityContext) {
        service.validate(creationContainer, securityContext);
        return service.createCity(creationContainer, securityContext);
    }


    @DELETE
    @Operation(summary = "deleteCity", description = "Deletes City")
    @IOperation(Name = "deleteCity", Description = "Deletes City")
    @Path("deleteCity/{id}")
    public void deleteCity(
            @HeaderParam("authenticationKey") String authenticationKey,
            @PathParam("id") String id, @Context SecurityContext securityContext) {
        service.deleteCity(id, securityContext);
    }
}