package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.dashboard.model.DataMapper;
import com.flexicore.ui.dashboard.request.DataMapperCreate;
import com.flexicore.ui.dashboard.request.DataMapperFilter;
import com.flexicore.ui.dashboard.request.DataMapperUpdate;
import com.flexicore.ui.dashboard.service.DataMapperService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


/**
 * Created by Asaf on 04/06/2017.
 */


@OperationsInside
@RestController
@RequestMapping("plugins/DataMapper")
@Tag(name = "DataMapper")
@Tag(name = "Presets")
@Extension
@Component
public class DataMapperController implements Plugin {

	
	@Autowired
	private DataMapperService service;

	
	
	@Operation(summary = "getAllDataMapper", description = "returns all DataMapper")
	@PostMapping("getAllDataMapper")
	public PaginationResponse<DataMapper> getAllDataMapper(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			DataMapperFilter dataMapperFilter,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(dataMapperFilter, securityContext);
		return service.getAllDataMapper(dataMapperFilter, securityContext);

	}

	
	
	@Operation(summary = "updateDataMapper", description = "Updates Dashbaord")
	@PutMapping("updateDataMapper")
	public DataMapper updateDataMapper(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			DataMapperUpdate updateDataMapper, @RequestAttribute SecurityContextBase securityContext) {
		DataMapper dataMapper = updateDataMapper.getId() != null ? service.getByIdOrNull(
				updateDataMapper.getId(), DataMapper.class, null, securityContext) : null;
		if (dataMapper == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no ui field with id  "
					+ updateDataMapper.getId());
		}
		updateDataMapper.setDataMapper(dataMapper);
		service.validate(updateDataMapper, securityContext);

		return service.updateDataMapper(updateDataMapper, securityContext);

	}

	
	
	@Operation(summary = "createDataMapper", description = "Creates DataMapper ")
	@PostMapping("createDataMapper")
	public DataMapper createDataMapper(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			DataMapperCreate createDataMapper, @RequestAttribute SecurityContextBase securityContext) {
		service.validate(createDataMapper, securityContext);
		return service.createDataMapper(createDataMapper, securityContext);

	}


}
