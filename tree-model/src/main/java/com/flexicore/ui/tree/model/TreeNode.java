package com.flexicore.ui.tree.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.ui.model.Preset;

import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.file.model.FileResource;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class TreeNode extends Baseclass {

    @ManyToOne(targetEntity = TreeNode.class)
    private TreeNode parent;
    private boolean staticChildren;
    private boolean eager;
    private boolean invisible;
    private boolean allowFilteringEditing;
    private boolean inMap;
    @ManyToOne(targetEntity = Preset.class)
    private Preset presenter;
    @ManyToOne(targetEntity = FileResource.class)
    private FileResource icon;


    @ManyToOne(targetEntity = DynamicExecution.class)
    private DynamicExecution dynamicExecution;
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> jsonNode=new HashMap<>();

    @Lob
    private String contextString;

    @OneToMany(targetEntity = TreeNode.class,mappedBy = "parent")
    @JsonIgnore
    private List<TreeNode> children=new ArrayList<>();

    @ManyToOne(targetEntity = TreeNode.class)
    public TreeNode getParent() {
        return parent;
    }

    public TreeNode setParent(TreeNode parent) {
        this.parent = parent;
        return this;
    }

    @OneToMany(targetEntity = TreeNode.class,mappedBy = "parent")
    @JsonIgnore
    public List<TreeNode> getChildren() {
        return children;
    }

    public TreeNode setChildren(List<TreeNode> children) {
        this.children = children;
        return this;
    }

    @ManyToOne(targetEntity = DynamicExecution.class)
    public DynamicExecution getDynamicExecution() {
        return dynamicExecution;
    }

    public <T extends TreeNode> T setDynamicExecution(DynamicExecution dynamicExecution) {
        this.dynamicExecution = dynamicExecution;
        return (T) this;
    }

    @Lob
    public String getContextString() {
        return contextString;
    }

    public TreeNode setContextString(String contextString) {
        this.contextString = contextString;
        return this;
    }

    public boolean isStaticChildren() {
        return staticChildren;
    }

    public TreeNode setStaticChildren(boolean staticChildren) {
        this.staticChildren = staticChildren;
        return this;
    }


    public boolean isEager() {
        return eager;
    }

    public TreeNode setEager(boolean egear) {
        this.eager = egear;
        return this;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public <T extends TreeNode> T setInvisible(boolean invisible) {
        this.invisible = invisible;
        return (T) this;
    }

    public boolean isAllowFilteringEditing() {
        return allowFilteringEditing;
    }

    public <T extends TreeNode> T setAllowFilteringEditing(boolean allowFilteringEditing) {
        this.allowFilteringEditing = allowFilteringEditing;
        return (T) this;
    }

    public boolean isInMap() {
        return inMap;
    }

    public <T extends TreeNode> T setInMap(boolean inMap) {
        this.inMap = inMap;
        return (T) this;
    }

    @ManyToOne(targetEntity = Preset.class)
    public Preset getPresenter() {
        return presenter;
    }

    public <T extends TreeNode> T setPresenter(Preset presenter) {
        this.presenter = presenter;
        return (T) this;
    }

    @ManyToOne(targetEntity = FileResource.class)
    public FileResource getIcon() {
        return icon;
    }

    public <T extends TreeNode> T setIcon(FileResource icon) {
        this.icon = icon;
        return (T) this;
    }

    @JsonIgnore
    @Column(columnDefinition = "jsonb")
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


    public <T extends TreeNode> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }
}
