package com.flexicore.ui.tree.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.tree.model.TreeNode;

public class TreeNodeUpdate extends TreeNodeCreate {

    private String nodeId;
    @JsonIgnore
    private TreeNode treeNode;

    public String getNodeId() {
        return nodeId;
    }

    public <T extends TreeNodeUpdate> T setNodeId(String nodeId) {
        this.nodeId = nodeId;
        return (T) this;
    }

    @JsonIgnore
    public TreeNode getTreeNode() {
        return treeNode;
    }

    public <T extends TreeNodeUpdate> T setTreeNode(TreeNode treeNode) {
        this.treeNode = treeNode;
        return (T) this;
    }
}
