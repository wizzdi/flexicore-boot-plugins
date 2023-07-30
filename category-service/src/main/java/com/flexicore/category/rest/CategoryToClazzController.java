package com.flexicore.category.rest;

import com.flexicore.annotations.OperationsInside;


import com.flexicore.category.model.CategoryToClazz;
import com.flexicore.category.request.CategoryToClazzCreate;
import com.flexicore.category.request.CategoryToClazzFilter;
import com.flexicore.category.request.CategoryToClazzUpdate;
import com.flexicore.category.service.CategoryToClazzService;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.flexicore.security.SecurityContextBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;





@OperationsInside
@RestController
@RequestMapping("plugins/CategoryToClazz")
@Tag(name = "CategoryToClazz")
@Extension
@Component
public class CategoryToClazzController implements Plugin {

	
	@Autowired
	private CategoryToClazzService service;

	
	
	@Operation(summary = "getAllCategoryToClazz", description = "Lists all CategoryToClazz")
	@PostMapping("getAllCategoryToClazz")
	public PaginationResponse<CategoryToClazz> getAllCategoryToClazz(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody CategoryToClazzFilter filtering,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(filtering, securityContext);
		return service.getAllCategoryToClazz(filtering, securityContext);
	}

	
	
	@PostMapping("/createCategoryToClazz")
	@Operation(summary = "createCategoryToClazz", description = "Creates CategoryToClazz")
	public CategoryToClazz createCategoryToClazz(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody CategoryToClazzCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {

		service.validate(creationContainer, securityContext);

		return service.createCategoryToClazz(creationContainer, securityContext);
	}

	
	
	@PutMapping("/updateCategoryToClazz")
	@Operation(summary = "updateCategoryToClazz", description = "Updates CategoryToClazz")
	public CategoryToClazz updateCategoryToClazz(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody CategoryToClazzUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		CategoryToClazz CategoryToClazz = service.getByIdOrNull(updateContainer.getId(), CategoryToClazz.class, null, securityContext);
		if (CategoryToClazz == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no CategoryToClazz with id "
					+ updateContainer.getId());
		}
		updateContainer.setCategoryToClazz(CategoryToClazz);
		service.validate(updateContainer, securityContext);

		return service.updateCategoryToClazz(updateContainer, securityContext);
	}
}