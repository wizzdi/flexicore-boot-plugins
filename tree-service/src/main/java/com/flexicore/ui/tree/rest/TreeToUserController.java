/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.ui.tree.rest;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.ui.tree.request.SaveTreeNodeStatusRequest;
import com.flexicore.ui.tree.request.TreeNodeStatusRequest;
import com.flexicore.ui.tree.response.SaveTreeNodeStatusResponse;
import com.flexicore.ui.tree.response.TreeNodeStatusResponse;
import com.flexicore.ui.tree.service.TreeNodeToUserService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("plugins/treeToUser")

@OperationsInside
@RestController
@Extension

@Tag(name = "TreeToUser")
public class TreeToUserController implements Plugin {


	@Autowired
	
	private TreeNodeToUserService service;



	
	
	@Operation(summary = "saveTreeNodeStatus", description = "save Tree node Status, the TreeNode Status saves the status for the current user and allows a client to show the tree in the same expansion collapsing saved")
	@PostMapping("saveTreeNodeStatus")
	public SaveTreeNodeStatusResponse saveTreeNodeStatus(
			@org.springframework.web.bind.annotation.RequestBody
			@RequestBody(description = "Stores  a list of NodeID,boolean pairs") SaveTreeNodeStatusRequest saveTreeNodeStatusRequest,
			@RequestAttribute SecurityContext securityContext) {
		service.validate(saveTreeNodeStatusRequest,securityContext);

		return service.saveTreeNodeStatus(saveTreeNodeStatusRequest, securityContext);
	}



	
	
	@Operation(summary = "getTreeNodeStatus", description = "get Tree nodes Status, get the stored values for the list of nodes stored in saveTreeNodeStatus")
	@PostMapping("getTreeNodeStatus")
	public TreeNodeStatusResponse getTreeNodeStatus(
			@org.springframework.web.bind.annotation.RequestBody
			TreeNodeStatusRequest treeNodeStatusRequest,
			@RequestAttribute SecurityContext securityContext) {
		service.validate(treeNodeStatusRequest,securityContext);

		return service.getTreeNodeStatus(treeNodeStatusRequest, securityContext);
	}






}
