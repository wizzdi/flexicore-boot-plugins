package com.flexicore.ui.tree.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.tree.model.TreeNode;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.util.HashMap;
import java.util.Map;

public class TreeCreate extends BasicCreate {

    private Map<String, Object> jsonNode=new HashMap<>();

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

    @JsonIgnore
    public Map<String, Object> getJsonNode() {
        return this.jsonNode;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return this.jsonNode;
    }

    @JsonAnySetter
    public void add(String key, Object value) {
        jsonNode.put(key, value);
    }

    public <T extends TreeCreate> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }

}
