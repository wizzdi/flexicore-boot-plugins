/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.ui.tree.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.tree.model.Tree;
import com.flexicore.ui.tree.model.Tree_;
import com.flexicore.ui.tree.request.TreeCreate;
import com.flexicore.ui.tree.request.TreeFilter;
import com.flexicore.ui.tree.request.TreeUpdate;
import com.flexicore.ui.tree.service.TreeService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("plugins/tree")

@OperationsInside
@RestController
@Extension

@Tag(name = "Tree")
public class TreeController implements Plugin {

	@Autowired
	
	private TreeService service;


	
	
	@Operation(summary = "getAllTrees", description = "lists all trees")
	@PostMapping("getAllTrees")
	public PaginationResponse<Tree> getAllTrees(
			@RequestHeader("authenticationKey") String authenticationKey,@org.springframework.web.bind.annotation.RequestBody
			TreeFilter treeFilter,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(treeFilter,securityContext);

		return service.getAllTrees(treeFilter, securityContext);
	}

	
	
	@Operation(summary = "updateTree", description = "update tree by tree ID")
	@PutMapping("updateTree")
	public Tree updateTree(
			@RequestHeader("authenticationKey") String authenticationKey,@org.springframework.web.bind.annotation.RequestBody
			@RequestBody(description = "Provide treeId, root node ID , tree name , tree description") TreeUpdate updateTree,
			@RequestAttribute SecurityContextBase securityContext) {
		Tree tree=updateTree.getTreeId()!=null?service.getByIdOrNull(updateTree.getTreeId(),Tree.class, Tree_.security,securityContext):null;
		if(tree==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Tree with id "+updateTree.getTreeId());
		}
		updateTree.setTree(tree);
		service.validate(updateTree,securityContext);

		return service.updateTree(updateTree, securityContext);
	}


	
	
	@Operation(summary = "createTree", description = "create tree, provide tree name , description and root node")
	@PostMapping("createTree")
	public Tree createTree(
			@RequestHeader("authenticationKey") String authenticationKey,@org.springframework.web.bind.annotation.RequestBody
			@RequestBody(description = "Tree name, description, root node ID , root node should be created before the tree is created") TreeCreate treeCreationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(treeCreationContainer,securityContext);

		return service.createTree(treeCreationContainer, securityContext);
	}






}
