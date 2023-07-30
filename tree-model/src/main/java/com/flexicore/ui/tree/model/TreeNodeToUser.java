/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.ui.tree.model;

import com.flexicore.model.SecuredBasic;
import com.flexicore.model.SecurityUser;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;


@SuppressWarnings("serial")
@Entity

public class TreeNodeToUser extends SecuredBasic {

	@ManyToOne(targetEntity = TreeNode.class)
	private TreeNode treeNode;
	@ManyToOne(targetEntity = SecurityUser.class)
	private SecurityUser user;
	private boolean nodeOpen;


	@ManyToOne(targetEntity = TreeNode.class)
	public TreeNode getTreeNode() {
		return treeNode;
	}

	public <T extends TreeNodeToUser> T setTreeNode(TreeNode treeNode) {
		this.treeNode = treeNode;
		return (T) this;
	}

	@ManyToOne(targetEntity = SecurityUser.class)
	public SecurityUser getUser() {
		return user;
	}

	public <T extends TreeNodeToUser> T setUser(SecurityUser user) {
		this.user = user;
		return (T) this;
	}

	public boolean isNodeOpen() {
		return nodeOpen;
	}

	public <T extends TreeNodeToUser> T setNodeOpen(boolean nodeOpen) {
		this.nodeOpen = nodeOpen;
		return (T) this;
	}

}
