package com.flexicore.ui.tree.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.flexicore.ui.model.Preset;
import com.wizzdi.dynamic.properties.converter.DynamicColumnDefinition;
import com.wizzdi.dynamic.properties.converter.JsonConverter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Tree extends SecuredBasic {



    @ManyToOne(targetEntity = TreeNode.class)
    private TreeNode root;

    @Convert(converter = JsonConverter.class)
    private Map<String, Object> jsonNode=new HashMap<>();

    @ManyToOne(targetEntity = TreeNode.class)
    public TreeNode getRoot() {
        return root;
    }

    public Tree setRoot(TreeNode root) {
        this.root = root;
        return this;
    }

    @JsonIgnore
    @DynamicColumnDefinition
    @Convert(converter = JsonConverter.class)
    public Map<String, Object> getJsonNode() {
        return jsonNode;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return jsonNode;
    }

    @JsonAnySetter
    public void add(String key, Object value) {
        jsonNode.put(key, value);
    }


    public <T extends Tree> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }
}
