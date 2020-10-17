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
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RESTService;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.tree.model.TreeNode;
import com.flexicore.ui.tree.request.TreeNodeCreate;
import com.flexicore.ui.tree.request.TreeNodeFilter;
import com.flexicore.ui.tree.request.TreeNodeUpdate;
import com.flexicore.ui.tree.service.TreeNodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

@Path("/plugins/treeNode")
@RequestScoped
@Component
@OperationsInside
@Protected
@Extension
@PluginInfo(version = 1)
@Tag(name = "TreeNode")
public class TreeNodeRESTService implements RestServicePlugin {

	@Autowired
	@PluginInfo(version = 1)
	private TreeNodeService service;


	@POST
	@Produces("application/json")
	@Operation(summary = "getAllTreeNodes", description = "lists all children nodes, parent is specified in the TreeNodeFiler")
	@Path("getAllTreeNodes")
	public PaginationResponse<TreeNode> getAllTreeNodes(
			@HeaderParam("authenticationKey") String authenticationKey,
			TreeNodeFilter treeFilter,
			@Context SecurityContext securityContext) {
		service.validate(treeFilter,securityContext);

		return service.getAllTreeNodes(treeFilter, securityContext);
	}




	@POST
	@Produces("application/json")
	@Operation(summary = "createTreeNode", description = "create tree node")
	@Path("createTreeNode")
	public TreeNode createTreeNode(
			@HeaderParam("authenticationKey") String authenticationKey,
			TreeNodeCreate treeNodeCreationContainer,
			@Context SecurityContext securityContext) {
		service.validate(treeNodeCreationContainer,securityContext);

		return service.createTreeNode(treeNodeCreationContainer, securityContext);
	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateTreeNode", description = "update tree node")
	@Path("updateTreeNode")
	public TreeNode updateTreeNode(
			@HeaderParam("authenticationKey") String authenticationKey,
			TreeNodeUpdate treeNodeCreationContainer,
			@Context SecurityContext securityContext) {
		TreeNode treeNode=treeNodeCreationContainer.getNodeId()!=null?service.getByIdOrNull(treeNodeCreationContainer.getNodeId(),TreeNode.class,null,securityContext):null;
		if(treeNode==null){
			throw new BadRequestException("No Tree node with id "+treeNodeCreationContainer.getNodeId());
		}
		treeNodeCreationContainer.setTreeNode(treeNode);
		service.validate(treeNodeCreationContainer,securityContext);

		return service.updateTreeNode(treeNodeCreationContainer, securityContext);
	}




}
