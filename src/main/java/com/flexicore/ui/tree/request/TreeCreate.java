package com.flexicore.ui.tree.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.request.BaseclassCreate;
import com.flexicore.ui.tree.model.TreeNode;

public class TreeCreate extends BaseclassCreate {

    private String rootId;
    @JsonIgnore
    private TreeNode root;


    public String getRootId() {
        return rootId;
    }

    public TreeCreate setRootId(String rootId) {
        this.rootId = rootId;
        return this;
    }

    @JsonIgnore
    public TreeNode getRoot() {
        return root;
    }

    public TreeCreate setRoot(TreeNode root) {
        this.root = root;
        return this;
    }

}
