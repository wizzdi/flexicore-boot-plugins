package com.flexicore.ui.tree.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.ui.tree.model.TreeNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TreeNodeFilter extends FilteringInformationHolder {



    private Set<String> parentId=new HashSet<>();
    @JsonIgnore
    private List<TreeNode> parents;


    public Set<String> getParentId() {
        return parentId;
    }

    public <T extends TreeNodeFilter> T setParentId(Set<String> parentId) {
        this.parentId = parentId;
        return (T) this;
    }

    @JsonIgnore
    public List<TreeNode> getParents() {
        return parents;
    }

    public <T extends TreeNodeFilter> T setParents(List<TreeNode> parents) {
        this.parents = parents;
        return (T) this;
    }

    @Override
    public boolean supportingDynamic() {
        return true;
    }
}
