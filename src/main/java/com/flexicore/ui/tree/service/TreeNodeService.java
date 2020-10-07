package com.flexicore.ui.tree.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.FileResource;
import com.flexicore.model.Presenter;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.ui.tree.data.TreeNodeRepository;
import com.flexicore.ui.tree.model.TreeNode;
import com.flexicore.ui.tree.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;


@PluginInfo(version = 1)
@Component
public class TreeNodeService implements ServicePlugin {

    @Autowired
    private TreeNodeRepository treeNodeRepository;

    @Autowired
    private BaseclassNewService baseclassNewService;

    
    public TreeNode createTreeNode(TreeNodeCreate treeNodeCreate, SecurityContext securityContext) {
        TreeNode treeNode=new TreeNode(treeNodeCreate.getName(),securityContext);
        updateTreeNodeNoMerge(treeNodeCreate,treeNode);
        treeNodeRepository.merge(treeNode);
        return treeNode;
    }

    public void validate(TreeNodeCreate treeNodeCreate, SecurityContext securityContext) {
        baseclassNewService.validate(treeNodeCreate,securityContext);
        String parentId = treeNodeCreate.getParentId();
        TreeNode parent= parentId !=null?getByIdOrNull(parentId,TreeNode.class,null,securityContext):null;
        if(parent==null && parentId !=null){
            throw new BadRequestException("No Tree Node With id "+ parentId);
        }
        treeNodeCreate.setParent(parent);

        String dynamicExecutionId = treeNodeCreate.getDynamicExecutionId();
        DynamicExecution dynamicExecution= dynamicExecutionId !=null?getByIdOrNull(dynamicExecutionId,DynamicExecution.class,null,securityContext):null;
        if(dynamicExecution==null&& dynamicExecutionId !=null){
            throw new BadRequestException("No Dynamic Execution with id "+ dynamicExecutionId);
        }
        treeNodeCreate.setDynamicExecution(dynamicExecution);

        String presenterId = treeNodeCreate.getPresenterId();
        Presenter presenter= presenterId !=null?getByIdOrNull(presenterId,Presenter.class,null,securityContext):null;
        if(presenter==null && presenterId !=null){
            throw new BadRequestException("No Presenter With id "+ presenterId);
        }
        treeNodeCreate.setPresenter(presenter);

        String iconId = treeNodeCreate.getIconId();
        FileResource icon= iconId !=null?getByIdOrNull(iconId, FileResource.class,null,securityContext):null;
        if(icon==null && iconId !=null){
            throw new BadRequestException("No FileResource With id "+ iconId);
        }
        treeNodeCreate.setIcon(icon);


    }

    public void validate(TreeNodeFilter treeNodeFilter, SecurityContext securityContext) {
        baseclassNewService.validateFilter(treeNodeFilter,securityContext);
        Set<String> parentId = treeNodeFilter.getParentId();
        Map<String,TreeNode> parent= parentId.isEmpty()?new HashMap<>():listByIds(TreeNode.class,parentId,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
        parentId.removeAll(parent.keySet());
        if(!parentId.isEmpty()){
            throw new BadRequestException("No Tree Nodes With ids "+ parentId);
        }
        treeNodeFilter.setParents(new ArrayList<>(parent.values()));
    }

    
    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return treeNodeRepository.getByIdOrNull(id, c, batchString, securityContext);
    }

    
    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return treeNodeRepository.listByIds(c, ids, securityContext);
    }


    private boolean updateTreeNodeNoMerge(TreeNodeCreate treeNodeCreate, TreeNode treeNode) {
        boolean update=baseclassNewService.updateBaseclassNoMerge(treeNodeCreate,treeNode);
        if(treeNodeCreate.getEager()!=null && treeNodeCreate.getEager()!=treeNode.isEager()){
            treeNode.setEager(treeNodeCreate.getEager());
            update=true;
        }
        if(treeNodeCreate.getInvisible()!=null && treeNodeCreate.getInvisible()!=treeNode.isInvisible()){
            treeNode.setInvisible(treeNodeCreate.getInvisible());
            update=true;
        }

        if(treeNodeCreate.getAllowFilteringEditing()!=null && treeNodeCreate.getAllowFilteringEditing()!=treeNode.isAllowFilteringEditing()){
            treeNode.setAllowFilteringEditing(treeNodeCreate.getAllowFilteringEditing());
            update=true;
        }
        if(treeNodeCreate.getInMap()!=null && treeNodeCreate.getInMap()!=treeNode.isInMap()){
            treeNode.setInMap(treeNodeCreate.getInMap());
            update=true;
        }
        if(treeNodeCreate.getStaticChildren()!=null && treeNodeCreate.getStaticChildren()!=treeNode.isStaticChildren()){
            treeNode.setStaticChildren(treeNodeCreate.getStaticChildren());
            update=true;
        }

        if(treeNodeCreate.getName()!=null && !treeNodeCreate.getName().equals(treeNode.getName())){
            treeNode.setName(treeNodeCreate.getName());
            update=true;
        }

        if(treeNodeCreate.getDescription()!=null && !treeNodeCreate.getDescription().equals(treeNode.getDescription())){
            treeNode.setDescription(treeNodeCreate.getDescription());
            update=true;
        }

        if(treeNodeCreate.getContextString()!=null && !treeNodeCreate.getContextString().equals(treeNode.getContextString())){
            treeNode.setContextString(treeNodeCreate.getContextString());
            update=true;
        }

        if(treeNodeCreate.getParent()!=null &&( treeNode.getParent()==null ||!treeNodeCreate.getParent().getId().equals(treeNode.getParent().getId()))){
            treeNode.setParent(treeNodeCreate.getParent());
            update=true;
        }

        if(treeNodeCreate.getDynamicExecution()!=null &&( treeNode.getDynamicExecution()==null ||!treeNodeCreate.getDynamicExecution().getId().equals(treeNode.getDynamicExecution().getId()))){
            treeNode.setDynamicExecution(treeNodeCreate.getDynamicExecution());
            update=true;
        }

        if(treeNodeCreate.getPresenter()!=null &&( treeNode.getPresenter()==null ||!treeNodeCreate.getPresenter().getId().equals(treeNode.getPresenter().getId()))){
            treeNode.setPresenter(treeNodeCreate.getPresenter());
            update=true;
        }

        if(treeNodeCreate.getIcon()!=null &&( treeNode.getIcon()==null ||!treeNodeCreate.getIcon().getId().equals(treeNode.getIcon().getId()))){
            treeNode.setIcon(treeNodeCreate.getIcon());
            update=true;
        }
        return update;

    }


    
    public PaginationResponse<TreeNode> getAllTreeNodes(TreeNodeFilter treeFilter, SecurityContext securityContext) {
        List<TreeNode> list= treeNodeRepository.getAllTreeNodes(treeFilter,securityContext);
        long count= treeNodeRepository.countAllTreeNodes(treeFilter,securityContext);
        return new PaginationResponse<>(list,treeFilter,count);

    }


    
    public TreeNode updateTreeNode(TreeNodeUpdate treeNodeUpdate, SecurityContext securityContext) {
        TreeNode treeNode = treeNodeUpdate.getTreeNode();
        if(updateTreeNodeNoMerge(treeNodeUpdate, treeNode)){
            treeNodeRepository.merge(treeNode);
        }
        return treeNode;
    }

}
