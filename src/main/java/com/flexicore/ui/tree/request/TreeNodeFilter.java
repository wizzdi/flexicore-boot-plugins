package com.flexicore.ui.tree.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.tree.model.TreeNode;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TreeNodeFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;


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


    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends TreeNodeFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }
}
