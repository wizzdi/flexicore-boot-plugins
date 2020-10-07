package com.flexicore.category.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.category.model.Category;
import com.flexicore.category.model.CategoryFilter;
import com.flexicore.category.request.CategoryCreate;
import com.flexicore.category.request.CategoryUpdate;
import com.flexicore.category.service.CategoryService;
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
@Path("plugins/Category")
@Tag(name = "Category")
@Extension
@Component
public class CategoryRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private CategoryService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllCategories", description = "Lists all Categories")
	@Path("getAllCategories")
	public PaginationResponse<Category> getAllCategories(
			@HeaderParam("authenticationKey") String authenticationKey,
			CategoryFilter filtering,
			@Context SecurityContext securityContext) {
		service.validate(filtering, securityContext);
		return service.getAllCategories(filtering, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/createCategory")
	@Operation(summary = "createCategory", description = "Creates Category")
	public Category createCategory(
			@HeaderParam("authenticationKey") String authenticationKey,
			CategoryCreate creationContainer,
			@Context SecurityContext securityContext) {

		service.validate(creationContainer, securityContext);

		return service.createCategory(creationContainer, securityContext);
	}

	@PUT
	@Produces("application/json")
	@Path("/updateCategory")
	@Operation(summary = "updateCategory", description = "Updates Category")
	public Category updateCategory(
			@HeaderParam("authenticationKey") String authenticationKey,
			CategoryUpdate updateContainer,
			@Context SecurityContext securityContext) {
		Category Category = service.getByIdOrNull(updateContainer.getId(),
				Category.class, null, securityContext);
		if (Category == null) {
			throw new BadRequestException("no Category with id "
					+ updateContainer.getId());
		}
		updateContainer.setCategory(Category);
		service.validate(updateContainer, securityContext);

		return service.updateCategory(updateContainer, securityContext);
	}
}