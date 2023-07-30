/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.ui.tree.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.tree.model.TreeNode;
import com.flexicore.ui.tree.model.TreeNode_;
import com.flexicore.ui.tree.request.TreeNodeCreate;
import com.flexicore.ui.tree.request.TreeNodeFilter;
import com.flexicore.ui.tree.request.TreeNodeUpdate;
import com.flexicore.ui.tree.service.TreeNodeService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RequestMapping("/plugins/treeNode")

@OperationsInside
@RestController
@Extension

@Tag(name = "TreeNode")
public class TreeNodeController implements Plugin {

	@Autowired
	
	private TreeNodeService service;


	
	
	@Operation(summary = "getAllTreeNodes", description = "lists all children nodes, parent is specified in the TreeNodeFiler")
	@PostMapping("getAllTreeNodes")
	public PaginationResponse<TreeNode> getAllTreeNodes(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			TreeNodeFilter treeFilter,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(treeFilter,securityContext);

		return service.getAllTreeNodes(treeFilter, securityContext);
	}




	
	
	@Operation(summary = "createTreeNode", description = "create tree node")
	@PostMapping("createTreeNode")
	public TreeNode createTreeNode(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			TreeNodeCreate treeNodeCreationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(treeNodeCreationContainer,securityContext);

		return service.createTreeNode(treeNodeCreationContainer, securityContext);
	}


	
	@Operation(summary = "updateTreeNode", description = "update tree node")
	@PutMapping("updateTreeNode")
	public TreeNode updateTreeNode(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			TreeNodeUpdate treeNodeCreationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		TreeNode treeNode=treeNodeCreationContainer.getNodeId()!=null?service.getByIdOrNull(treeNodeCreationContainer.getNodeId(),TreeNode.class, TreeNode_.security,securityContext):null;
		if(treeNode==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Tree node with id "+treeNodeCreationContainer.getNodeId());
		}
		treeNodeCreationContainer.setTreeNode(treeNode);
		service.validate(treeNodeCreationContainer,securityContext);

		return service.updateTreeNode(treeNodeCreationContainer, securityContext);
	}




}
