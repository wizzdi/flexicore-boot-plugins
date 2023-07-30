package com.flexicore.ui.tree.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.model.Preset;
import com.flexicore.ui.model.Preset_;
import com.flexicore.ui.tree.data.TreeNodeRepository;
import com.flexicore.ui.tree.model.TreeNode;
import com.flexicore.ui.tree.model.TreeNode_;
import com.flexicore.ui.tree.request.TreeNodeCreate;
import com.flexicore.ui.tree.request.TreeNodeFilter;
import com.flexicore.ui.tree.request.TreeNodeUpdate;
import com.wizzdi.dynamic.properties.converter.DynamicPropertiesUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.model.FileResource_;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;


@Extension
@Component
public class TreeNodeService implements Plugin {

    @Autowired
    
    private TreeNodeRepository treeNodeRepository;

    @Autowired
    private BasicService basicService;

    
    public TreeNode createTreeNode(TreeNodeCreate treeNodeCreate, SecurityContextBase securityContext) {
        TreeNode treeNode=new TreeNode();
        treeNode.setId(UUID.randomUUID().toString());
        updateTreeNodeNoMerge(treeNodeCreate,treeNode);
        BaseclassService.createSecurityObjectNoMerge(treeNode,securityContext);
        treeNodeRepository.merge(treeNode);
        return treeNode;
    }

    public void validate(TreeNodeCreate treeNodeCreate, SecurityContextBase securityContext) {
        basicService.validate(treeNodeCreate,securityContext);
        String parentId = treeNodeCreate.getParentId();
        TreeNode parent= parentId !=null?getByIdOrNull(parentId,TreeNode.class, TreeNode_.security,securityContext):null;
        if(parent==null && parentId !=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Tree Node With id "+ parentId);
        }
        treeNodeCreate.setParent(parent);

        String dynamicExecutionId = treeNodeCreate.getDynamicExecutionId();
        DynamicExecution dynamicExecution= dynamicExecutionId !=null?getByIdOrNull(dynamicExecutionId,DynamicExecution.class, SecuredBasic_.security,securityContext):null;
        if(dynamicExecution==null&& dynamicExecutionId !=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Dynamic Execution with id "+ dynamicExecutionId);
        }
        treeNodeCreate.setDynamicExecution(dynamicExecution);

        String presenterId = treeNodeCreate.getPresenterId();
        Preset presenter= presenterId !=null?getByIdOrNull(presenterId, Preset.class, Preset_.security,securityContext):null;
        if(presenter==null && presenterId !=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Preset With id "+ presenterId);
        }
        treeNodeCreate.setPresenter(presenter);

        String iconId = treeNodeCreate.getIconId();
        FileResource icon= iconId !=null?getByIdOrNull(iconId, FileResource.class, FileResource_.security,securityContext):null;
        if(icon==null && iconId !=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No FileResource With id "+ iconId);
        }
        treeNodeCreate.setIcon(icon);


    }

    public void validate(TreeNodeFilter treeNodeFilter, SecurityContextBase securityContext) {
        basicService.validate(treeNodeFilter,securityContext);
        basicService.validate(treeNodeFilter.getBasicPropertiesFilter(),securityContext);
        Set<String> parentId = treeNodeFilter.getParentId();
        Map<String,TreeNode> parent= parentId.isEmpty()?new HashMap<>():listByIds(TreeNode.class,parentId,TreeNode_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
        parentId.removeAll(parent.keySet());
        if(!parentId.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Tree Nodes With ids "+ parentId);
        }
        treeNodeFilter.setParents(new ArrayList<>(parent.values()));
    }




    private boolean updateTreeNodeNoMerge(TreeNodeCreate treeNodeCreate, TreeNode treeNode) {
        boolean update=basicService.updateBasicNoMerge(treeNodeCreate,treeNode);
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

        Map<String, Object> map = DynamicPropertiesUtils.updateDynamic(treeNodeCreate.any(), treeNode.any());
        if (map != null) {
            treeNode.setJsonNode(map);
            update = true;
        }
        return update;

    }


    
    public PaginationResponse<TreeNode> getAllTreeNodes(TreeNodeFilter treeFilter, SecurityContextBase securityContext) {
        List<TreeNode> list= treeNodeRepository.getAllTreeNodes(treeFilter,securityContext);
        long count= treeNodeRepository.countAllTreeNodes(treeFilter,securityContext);
        return new PaginationResponse<>(list,treeFilter,count);

    }


    
    public TreeNode updateTreeNode(TreeNodeUpdate treeNodeUpdate, SecurityContextBase securityContext) {
        TreeNode treeNode = treeNodeUpdate.getTreeNode();
        if(updateTreeNodeNoMerge(treeNodeUpdate, treeNode)){
            treeNodeRepository.merge(treeNode);
        }
        return treeNode;
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return treeNodeRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return treeNodeRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return treeNodeRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return treeNodeRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return treeNodeRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return treeNodeRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return treeNodeRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        treeNodeRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        treeNodeRepository.massMerge(toMerge);
    }
}
