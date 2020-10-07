package com.flexicore.category.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.category.model.CategoryToBaseClass;
import com.flexicore.category.request.CategoryToBaseclassCreate;
import com.flexicore.category.request.CategoryToBaseclassFilter;
import com.flexicore.category.request.CategoryToBaseclassUpdate;
import com.flexicore.category.service.CategoryToBaseclassService;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/CategoryToBaseclass")
@Tag(name = "CategoryToBaseclass")
@Extension
@Component
public class CategoryToBaseclassRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private CategoryToBaseclassService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllCategoryToBaseclass", description = "Lists all CategoryToBaseclass")
	@Path("getAllCategoryToBaseclass")
	public PaginationResponse<CategoryToBaseClass> getAllCategoryToBaseclass(
			@HeaderParam("authenticationKey") String authenticationKey,
			CategoryToBaseclassFilter filtering,
			@Context SecurityContext securityContext) {
		service.validate(filtering, securityContext);
		return service.getAllCategoryToBaseclass(filtering, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/createCategoryToBaseclass")
	@Operation(summary = "createCategoryToBaseclass", description = "Creates CategoryToBaseclass")
	public CategoryToBaseClass createCategoryToBaseclass(
			@HeaderParam("authenticationKey") String authenticationKey,
			CategoryToBaseclassCreate creationContainer,
			@Context SecurityContext securityContext) {

		service.validate(creationContainer, securityContext);

		return service.createCategoryToBaseclass(creationContainer, securityContext);
	}

	@PUT
	@Produces("application/json")
	@Path("/updateCategoryToBaseclass")
	@Operation(summary = "updateCategoryToBaseclass", description = "Updates CategoryToBaseclass")
	public CategoryToBaseClass updateCategoryToBaseclass(
			@HeaderParam("authenticationKey") String authenticationKey,
			CategoryToBaseclassUpdate updateContainer,
			@Context SecurityContext securityContext) {
		CategoryToBaseClass CategoryToBaseclass = service.getByIdOrNull(updateContainer.getId(), CategoryToBaseClass.class, null, securityContext);
		if (CategoryToBaseclass == null) {
			throw new BadRequestException("no CategoryToBaseclass with id "
					+ updateContainer.getId());
		}
		updateContainer.setCategoryToBaseClass(CategoryToBaseclass);
		service.validate(updateContainer, securityContext);

		return service.updateCategoryToBaseclass(updateContainer, securityContext);
	}
}