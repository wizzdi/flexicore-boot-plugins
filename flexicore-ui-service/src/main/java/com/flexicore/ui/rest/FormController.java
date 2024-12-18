package com.flexicore.ui.rest;

import com.flexicore.annotations.OperationsInside;

import com.flexicore.ui.model.Form_;
import com.wizzdi.flexicore.security.response.PaginationResponse;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.ui.model.Form;
import com.flexicore.ui.request.FormCopy;
import com.flexicore.ui.request.FormCreate;
import com.flexicore.ui.request.FormFiltering;
import com.flexicore.ui.request.FormUpdate;
import com.flexicore.ui.service.FormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;




import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Asaf on 04/06/2017.
 */


@OperationsInside
@RestController
@RequestMapping("plugins/Forms")
@Tag(name = "Forms", description = "Form support free definition of grids using Dynamic Execution as source of data")
@Tag(name = "Presets")
@Extension
@Component
public class FormController implements Plugin {

	
	@Autowired
	private FormService service;

	

	@Operation(summary = "getAllForms", description = "returns all Forms")
	@PostMapping("getAllForms")
	public PaginationResponse<Form> getAllForms(
			 @RequestBody
			FormFiltering formFiltering,
			@RequestAttribute SecurityContext securityContext) {
		return service.getAllForms(formFiltering, securityContext);

	}

	

	@Operation(summary = "updateForm", description = "Updates Dashbaord")
	@PutMapping("updateForm")
	public Form updateForm(
			
			@RequestBody FormUpdate updateForm, @RequestAttribute SecurityContext securityContext) {
		Form form = updateForm.getId() != null ? service.getByIdOrNull(updateForm.getId(), Form.class,securityContext) : null;
		if (form == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Form with id  " + updateForm.getId());
		}
		updateForm.setForm(form);
		service.validate(updateForm, securityContext);

		return service.updateForm(updateForm, securityContext);

	}

	

	@Operation(summary = "createForm", description = "Creates Form ")
	@PostMapping("createForm")
	public Form createForm(
			 @RequestBody
			FormCreate createForm, @RequestAttribute SecurityContext securityContext) {
		service.validate(createForm, securityContext);
		return service.createForm(createForm, securityContext);

	}

	

	@Operation(summary = "copyForm", description = "Copies Form")
	@PostMapping("copyForm")
	public Form copyForm(
			 @RequestBody
			FormCopy formCopy, @RequestAttribute SecurityContext securityContext) {
		service.validate(formCopy, securityContext);
		return service.copyForm(formCopy, securityContext);

	}

}
