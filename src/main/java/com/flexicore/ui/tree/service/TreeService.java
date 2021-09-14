package com.flexicore.ui.tree.service;


import com.flexicore.model.Basic;
import com.flexicore.ui.tree.model.TreeNode_;
import com.wizzdi.dynamic.properties.converter.DynamicPropertiesUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.flexicore.ui.tree.data.TreeRepository;
import com.flexicore.ui.tree.model.Tree;
import com.flexicore.ui.tree.model.TreeNode;
import com.flexicore.ui.tree.request.TreeCreate;
import com.flexicore.ui.tree.request.TreeFilter;
import com.flexicore.ui.tree.request.TreeUpdate;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


@Extension
@Component
public class TreeService implements Plugin {

    @Autowired
    
    private TreeRepository treeRepository;
    @Autowired
    private BasicService basicService;



    private boolean updateTreeNoMerge(TreeCreate treeCreate, Tree tree) {
        boolean update = basicService.updateBasicNoMerge(treeCreate,tree);
        if (treeCreate.getRoot() != null && (tree.getRoot() == null || !treeCreate.getRoot().getId().equals(tree.getRoot().getId()))) {
            tree.setRoot(treeCreate.getRoot());
            update = true;
        }
        Map<String, Object> map = DynamicPropertiesUtils.updateDynamic(treeCreate.any(), tree.any());
        if (map != null) {
            tree.setJsonNode(map);
            update = true;
        }
        return update;

    }


    public Tree createTree(TreeCreate treeCreate, SecurityContextBase securityContext) {

        Tree tree = new Tree();
        tree.setId(UUID.randomUUID().toString());
        updateTreeNoMerge(treeCreate, tree);
        BaseclassService.createSecurityObjectNoMerge(tree,securityContext);
        treeRepository.merge(tree);
        return tree;
    }


    public PaginationResponse<Tree> getAllTrees(TreeFilter treeFilter, SecurityContextBase securityContext) {
        List<Tree> list = treeRepository.getAllTrees(treeFilter, securityContext);
        long count = treeRepository.countAllTrees(treeFilter, securityContext);
        return new PaginationResponse<>(list, treeFilter, count);
    }


    public Tree updateTree(TreeUpdate updateTree, SecurityContextBase securityContext) {

        Tree tree = updateTree.getTree();
        if (updateTreeNoMerge(updateTree, tree)) {
            treeRepository.merge(tree);
        }
        return tree;
    }

    public void validate(TreeFilter treeFilter, SecurityContextBase securityContext) {
        basicService.validate(treeFilter,securityContext);

    }

    public void validate(TreeCreate treeCreate, SecurityContextBase securityContext) {
        basicService.validate(treeCreate,securityContext);
        String rootId = treeCreate.getRootId();
        TreeNode root = rootId != null ? getByIdOrNull(rootId, TreeNode.class, TreeNode_.security, securityContext) : null;
        if (root == null && rootId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Tree Node With id " + rootId);
        }
        treeCreate.setRoot(root);

    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return treeRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return treeRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return treeRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return treeRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return treeRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return treeRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return treeRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        treeRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        treeRepository.massMerge(toMerge);
    }
}
