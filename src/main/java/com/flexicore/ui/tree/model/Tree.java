package com.flexicore.ui.tree.model;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Tree extends SecuredBasic {



    @ManyToOne(targetEntity = TreeNode.class)
    private TreeNode root;


    @ManyToOne(targetEntity = TreeNode.class)
    public TreeNode getRoot() {
        return root;
    }

    public Tree setRoot(TreeNode root) {
        this.root = root;
        return this;
    }
}
