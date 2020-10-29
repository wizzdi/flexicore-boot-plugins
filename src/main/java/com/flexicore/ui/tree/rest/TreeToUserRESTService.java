/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.ui.tree.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.Protected;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.tree.request.SaveTreeNodeStatusRequest;
import com.flexicore.ui.tree.request.TreeNodeStatusRequest;
import com.flexicore.ui.tree.response.SaveTreeNodeStatusResponse;
import com.flexicore.ui.tree.response.TreeNodeStatusResponse;
import com.flexicore.ui.tree.service.TreeNodeToUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path("plugins/treeToUser")
@RequestScoped
@Component
@OperationsInside
@Protected
@Extension
@PluginInfo(version = 1)
@Tag(name = "TreeToUser")
public class TreeToUserRESTService implements RestServicePlugin {


	@Autowired
	@PluginInfo(version = 1)
	private TreeNodeToUserService service;



	@POST
	@Produces("application/json")
	@Operation(summary = "saveTreeNodeStatus", description = "save Tree node Status, the TreeNode Status saves the status for the current user and allows a client to show the tree in the same expansion collapsing saved")
	@Path("saveTreeNodeStatus")
	public SaveTreeNodeStatusResponse saveTreeNodeStatus(
			@HeaderParam("authenticationKey") String authenticationKey,
			@RequestBody(description = "Stores  a list of NodeID,boolean pairs",required = true) SaveTreeNodeStatusRequest saveTreeNodeStatusRequest,
			@Context SecurityContext securityContext) {
		service.validate(saveTreeNodeStatusRequest,securityContext);

		return service.saveTreeNodeStatus(saveTreeNodeStatusRequest, securityContext);
	}



	@POST
	@Produces("application/json")
	@Operation(summary = "getTreeNodeStatus", description = "get Tree nodes Status, get the stored values for the list of nodes stored in saveTreeNodeStatus")
	@Path("getTreeNodeStatus")
	public TreeNodeStatusResponse getTreeNodeStatus(
			@HeaderParam("authenticationKey") String authenticationKey,
			TreeNodeStatusRequest treeNodeStatusRequest,
			@Context SecurityContext securityContext) {
		service.validate(treeNodeStatusRequest,securityContext);

		return service.getTreeNodeStatus(treeNodeStatusRequest, securityContext);
	}






}
