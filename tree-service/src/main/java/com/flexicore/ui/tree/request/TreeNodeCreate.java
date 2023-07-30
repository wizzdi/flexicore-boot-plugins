package com.flexicore.ui.tree.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.model.Preset;
import com.flexicore.ui.tree.model.TreeNode;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

@Schema(description = "Describes a treenode to be created or updated")
public class TreeNodeCreate extends BasicCreate {

    private String parentId;
    @JsonIgnore
    private TreeNode parent;
    private String dynamicExecutionId;
    @JsonIgnore
    private DynamicExecution dynamicExecution;
    private Boolean eager;
    private Boolean staticChildren;
    private Boolean invisible;
    private String contextString;
    private Boolean allowFilteringEditing;
    private Boolean inMap;
    private String presenterId;
    @JsonIgnore
    private Preset presenter;
    private String iconId;
    @JsonIgnore
    private FileResource icon;
    private Map<String, Object> jsonNode=new HashMap<>();



    @Schema(description = "Parent Node Id , or null ")
    public String getParentId() {
        return parentId;
    }

    public TreeNodeCreate setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    @JsonIgnore
    public TreeNode getParent() {
        return parent;
    }

    public TreeNodeCreate setParent(TreeNode parent) {
        this.parent = parent;
        return this;
    }

    @Schema(description = "dynamic execution id to be saved on this tree node")
    public String getDynamicExecutionId() {
        return dynamicExecutionId;
    }

    public <T extends TreeNodeCreate> T setDynamicExecutionId(String dynamicExecutionId) {
        this.dynamicExecutionId = dynamicExecutionId;
        return (T) this;
    }

    @JsonIgnore
    public DynamicExecution getDynamicExecution() {
        return dynamicExecution;
    }

    public <T extends TreeNodeCreate> T setDynamicExecution(DynamicExecution dynamicExecution) {
        this.dynamicExecution = dynamicExecution;
        return (T) this;
    }

    @Schema(description = "this is for client use, if true, get all children nodes when node is displayed")
    public Boolean getEager() {
        return eager;
    }

    public TreeNodeCreate setEager(Boolean eager) {
        this.eager = eager;
        return this;
    }
    @Schema(description = "Static node, that is children are not dynamically created by a filter, children are explicitly  specified ")
    public Boolean getStaticChildren() {
        return staticChildren;
    }

    public TreeNodeCreate setStaticChildren(Boolean staticChildren) {
        this.staticChildren = staticChildren;
        return this;
    }
    @Schema(description = "This is for client use (JSON makes sense here) , use can be any, for example for setting optional filter at run time, or for specifying behavior for up stream nodes in filters")
    public String getContextString() {
        return contextString;
    }

    public <T extends TreeNodeCreate> T setContextString(String contextString) {
        this.contextString = contextString;
        return (T) this;
    }
    @Schema(description = "A node can be invisible, makes sense for root or static nodes only")
    public Boolean getInvisible() {
        return invisible;
    }

    public <T extends TreeNodeCreate> T setInvisible(Boolean invisible) {
        this.invisible = invisible;
        return (T) this;
    }
    @Schema(description = "If true, allow users , at run time, to change filters on dynamic nodes.")
    public Boolean getAllowFilteringEditing() {
        return allowFilteringEditing;
    }

    public <T extends TreeNodeCreate> T setAllowFilteringEditing(Boolean allowFilteringEditing) {
        this.allowFilteringEditing = allowFilteringEditing;
        return (T) this;
    }

    @Schema(description = "If true, retrieved leaves  are displayed on the map, for this, the type of nodes must be ? extends EquipmentShort")
    public Boolean getInMap() {
        return inMap;
    }

    public <T extends TreeNodeCreate> T setInMap(Boolean inMap) {
        this.inMap = inMap;
        return (T) this;
    }

    @Schema(description = "Presenter id")
    public String getPresenterId() {
        return presenterId;
    }

    public <T extends TreeNodeCreate> T setPresenterId(String presenterId) {
        this.presenterId = presenterId;
        return (T) this;
    }

    @JsonIgnore
    public Preset getPresenter() {
        return presenter;
    }

    public <T extends TreeNodeCreate> T setPresenter(Preset presenter) {
        this.presenter = presenter;
        return (T) this;
    }
    @Schema(description = "tree nodes icon - this is a fileResource id")
    public String getIconId() {
        return iconId;
    }

    public <T extends TreeNodeCreate> T setIconId(String iconId) {
        this.iconId = iconId;
        return (T) this;
    }

    @JsonIgnore
    public FileResource getIcon() {
        return icon;
    }

    public <T extends TreeNodeCreate> T setIcon(FileResource icon) {
        this.icon = icon;
        return (T) this;
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

    public <T extends TreeNodeCreate> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }


}
