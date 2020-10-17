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
import com.flexicore.ui.tree.model.Tree;
import com.flexicore.ui.tree.request.*;
import com.flexicore.ui.tree.service.TreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

@Path("plugins/tree")
@RequestScoped
@Component
@OperationsInside
@Protected
@Extension
@PluginInfo(version = 1)
@Tag(name = "Tree")
public class TreeRESTService implements RestServicePlugin {

	@Autowired
	@PluginInfo(version = 1)
	private TreeService service;


	@POST
	@Produces("application/json")
	@Operation(summary = "getAllTrees", description = "lists all trees")
	@Path("getAllTrees")
	public PaginationResponse<Tree> getAllTrees(
			@HeaderParam("authenticationKey") String authenticationKey,
			TreeFilter treeFilter,
			@Context SecurityContext securityContext) {
		service.validate(treeFilter,securityContext);

		return service.getAllTrees(treeFilter, securityContext);
	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateTree", description = "update tree by tree ID")
	@Path("updateTree")
	public Tree updateTree(
			@HeaderParam("authenticationKey") String authenticationKey,
			@RequestBody(description = "Provide treeId, root node ID , tree name , tree description") TreeUpdate updateTree,
			@Context SecurityContext securityContext) {
		Tree tree=updateTree.getTreeId()!=null?service.getByIdOrNull(updateTree.getTreeId(),Tree.class,null,securityContext):null;
		if(tree==null){
			throw new BadRequestException("No Tree with id "+updateTree.getTreeId());
		}
		updateTree.setTree(tree);
		service.validate(updateTree,securityContext);

		return service.updateTree(updateTree, securityContext);
	}


	@POST
	@Produces("application/json")
	@Operation(summary = "createTree", description = "create tree, provide tree name , description and root node")
	@Path("createTree")
	public Tree createTree(
			@HeaderParam("authenticationKey") String authenticationKey,
			@RequestBody(description = "Tree name, description, root node ID , root node should be created before the tree is created") TreeCreate treeCreationContainer,
			@Context SecurityContext securityContext) {
		service.validate(treeCreationContainer,securityContext);

		return service.createTree(treeCreationContainer, securityContext);
	}






}
