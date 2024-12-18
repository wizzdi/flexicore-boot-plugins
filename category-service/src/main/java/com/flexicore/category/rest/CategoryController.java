package com.flexicore.category.rest;

import com.flexicore.annotations.OperationsInside;


import com.flexicore.category.model.Category;
import com.flexicore.category.request.CategoryFilter;
import com.flexicore.category.request.CategoryCreate;
import com.flexicore.category.request.CategoryUpdate;
import com.flexicore.category.service.CategoryService;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.wizzdi.flexicore.security.configuration.SecurityContext;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@OperationsInside
@RestController
@RequestMapping("plugins/Category")
@Tag(name = "Category")
@Extension
@Component
public class CategoryController implements Plugin {

	
	@Autowired
	private CategoryService service;

	
	
	@Operation(summary = "getAllCategories", description = "Lists all Categories")
	@PostMapping("getAllCategories")
	public PaginationResponse<Category> getAllCategories(
			
			@RequestBody CategoryFilter filtering,
			@RequestAttribute SecurityContext securityContext) {
		service.validate(filtering, securityContext);
		return service.getAllCategories(filtering, securityContext);
	}

	
	
	@PostMapping("/createCategory")
	@Operation(summary = "createCategory", description = "Creates Category")
	public Category createCategory(
			
			@RequestBody CategoryCreate creationContainer,
			@RequestAttribute SecurityContext securityContext) {

		service.validate(creationContainer, securityContext);

		return service.createCategory(creationContainer, securityContext);
	}

	
	
	@PutMapping("/updateCategory")
	@Operation(summary = "updateCategory", description = "Updates Category")
	public Category updateCategory(
			
			@RequestBody CategoryUpdate updateContainer,
			@RequestAttribute SecurityContext securityContext) {
		Category Category = service.getByIdOrNull(updateContainer.getId(),
				Category.class, null, securityContext);
		if (Category == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Category with id "
					+ updateContainer.getId());
		}
		updateContainer.setCategory(Category);
		service.validate(updateContainer, securityContext);

		return service.updateCategory(updateContainer, securityContext);
	}
}
