package com.flexicore.ui.tree.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.tree.model.Tree;

public class TreeUpdate extends TreeCreate {

    private String treeId;
    @JsonIgnore
    private Tree tree;

    public String getTreeId() {
        return treeId;
    }

    public <T extends TreeUpdate> T setTreeId(String treeId) {
        this.treeId = treeId;
        return (T) this;
    }

    @JsonIgnore
    public Tree getTree() {
        return tree;
    }

    public <T extends TreeUpdate> T setTree(Tree tree) {
        this.tree = tree;
        return (T) this;
    }
}
