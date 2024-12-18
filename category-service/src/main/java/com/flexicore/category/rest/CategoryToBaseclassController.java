package com.flexicore.category.rest;

import com.flexicore.annotations.OperationsInside;


import com.flexicore.category.model.CategoryToBaseClass;
import com.flexicore.category.request.CategoryToBaseclassCreate;
import com.flexicore.category.request.CategoryToBaseclassFilter;
import com.flexicore.category.request.CategoryToBaseclassUpdate;
import com.flexicore.category.service.CategoryToBaseclassService;


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
@RequestMapping("plugins/CategoryToBaseclass")
@Tag(name = "CategoryToBaseclass")
@Extension
@Component
public class CategoryToBaseclassController implements Plugin {

	
	@Autowired
	private CategoryToBaseclassService service;

	
	
	@Operation(summary = "getAllCategoryToBaseclass", description = "Lists all CategoryToBaseclass")
	@PostMapping("getAllCategoryToBaseclass")
	public PaginationResponse<CategoryToBaseClass> getAllCategoryToBaseclass(
			
			@RequestBody CategoryToBaseclassFilter filtering,
			@RequestAttribute SecurityContext securityContext) {
		service.validate(filtering, securityContext);
		return service.getAllCategoryToBaseclass(filtering, securityContext);
	}

	
	
	@PostMapping("/createCategoryToBaseclass")
	@Operation(summary = "createCategoryToBaseclass", description = "Creates CategoryToBaseclass")
	public CategoryToBaseClass createCategoryToBaseclass(
			
			@RequestBody CategoryToBaseclassCreate creationContainer,
			@RequestAttribute SecurityContext securityContext) {

		service.validate(creationContainer, securityContext);

		return service.createCategoryToBaseclass(creationContainer, securityContext);
	}

	
	
	@PutMapping("/updateCategoryToBaseclass")
	@Operation(summary = "updateCategoryToBaseclass", description = "Updates CategoryToBaseclass")
	public CategoryToBaseClass updateCategoryToBaseclass(
			
			@RequestBody CategoryToBaseclassUpdate updateContainer,
			@RequestAttribute SecurityContext securityContext) {
		CategoryToBaseClass CategoryToBaseclass = service.getByIdOrNull(updateContainer.getId(), CategoryToBaseClass.class, null, securityContext);
		if (CategoryToBaseclass == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no CategoryToBaseclass with id "
					+ updateContainer.getId());
		}
		updateContainer.setCategoryToBaseClass(CategoryToBaseclass);
		service.validate(updateContainer, securityContext);

		return service.updateCategoryToBaseclass(updateContainer, securityContext);
	}
}
