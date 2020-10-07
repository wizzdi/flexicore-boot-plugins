package com.flexicore.category.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.category.model.CategoryToClazz;
import com.flexicore.category.request.CategoryToClazzCreate;
import com.flexicore.category.request.CategoryToClazzFilter;
import com.flexicore.category.request.CategoryToClazzUpdate;
import com.flexicore.category.service.CategoryToClazzService;
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
@Path("plugins/CategoryToClazz")
@Tag(name = "CategoryToClazz")
@Extension
@Component
public class CategoryToClazzRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private CategoryToClazzService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllCategoryToClazz", description = "Lists all CategoryToClazz")
	@Path("getAllCategoryToClazz")
	public PaginationResponse<CategoryToClazz> getAllCategoryToClazz(
			@HeaderParam("authenticationKey") String authenticationKey,
			CategoryToClazzFilter filtering,
			@Context SecurityContext securityContext) {
		service.validate(filtering, securityContext);
		return service.getAllCategoryToClazz(filtering, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/createCategoryToClazz")
	@Operation(summary = "createCategoryToClazz", description = "Creates CategoryToClazz")
	public CategoryToClazz createCategoryToClazz(
			@HeaderParam("authenticationKey") String authenticationKey,
			CategoryToClazzCreate creationContainer,
			@Context SecurityContext securityContext) {

		service.validate(creationContainer, securityContext);

		return service.createCategoryToClazz(creationContainer, securityContext);
	}

	@PUT
	@Produces("application/json")
	@Path("/updateCategoryToClazz")
	@Operation(summary = "updateCategoryToClazz", description = "Updates CategoryToClazz")
	public CategoryToClazz updateCategoryToClazz(
			@HeaderParam("authenticationKey") String authenticationKey,
			CategoryToClazzUpdate updateContainer,
			@Context SecurityContext securityContext) {
		CategoryToClazz CategoryToClazz = service.getByIdOrNull(updateContainer.getId(), CategoryToClazz.class, null, securityContext);
		if (CategoryToClazz == null) {
			throw new BadRequestException("no CategoryToClazz with id "
					+ updateContainer.getId());
		}
		updateContainer.setCategoryToClazz(CategoryToClazz);
		service.validate(updateContainer, securityContext);

		return service.updateCategoryToClazz(updateContainer, securityContext);
	}
}