package com.flexicore.ui.tree.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.ui.tree.data.TreeRepository;
import com.flexicore.ui.tree.model.Tree;
import com.flexicore.ui.tree.model.TreeNode;
import com.flexicore.ui.tree.request.TreeCreate;
import com.flexicore.ui.tree.request.TreeFilter;
import com.flexicore.ui.tree.request.TreeUpdate;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Set;


@PluginInfo(version = 1)
@Extension
@Component
public class TreeService implements ServicePlugin {

    @Autowired
    @PluginInfo(version = 1)
    private TreeRepository treeRepository;
    @Autowired
    private BaseclassNewService baseclassNewService;


    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return treeRepository.getByIdOrNull(id, c, batchString, securityContext);
    }


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return treeRepository.listByIds(c, ids, securityContext);
    }

    private boolean updateTreeNoMerge(TreeCreate treeCreate, Tree treeNode) {
        boolean update = baseclassNewService.updateBaseclassNoMerge(treeCreate,treeNode);


        if (treeCreate.getName() != null && !treeCreate.getName().equals(treeNode.getName())) {
            treeNode.setName(treeCreate.getName());
            update = true;
        }

        if (treeCreate.getDescription() != null && !treeCreate.getDescription().equals(treeNode.getDescription())) {
            treeNode.setDescription(treeCreate.getDescription());
            update = true;
        }

        if (treeCreate.getRoot() != null && (treeNode.getRoot() == null || !treeCreate.getRoot().getId().equals(treeNode.getRoot().getId()))) {
            treeNode.setRoot(treeCreate.getRoot());
            update = true;
        }
        return update;

    }


    public Tree createTree(TreeCreate treeCreate, SecurityContext securityContext) {

        Tree tree = new Tree(treeCreate.getName(), securityContext);
        updateTreeNoMerge(treeCreate, tree);
        treeRepository.merge(tree);
        return tree;
    }


    public PaginationResponse<Tree> getAllTrees(TreeFilter treeFilter, SecurityContext securityContext) {
        List<Tree> list = treeRepository.getAllTrees(treeFilter, securityContext);
        long count = treeRepository.countAllTrees(treeFilter, securityContext);
        return new PaginationResponse<>(list, treeFilter, count);
    }


    public Tree updateTree(TreeUpdate updateTree, SecurityContext securityContext) {

        Tree tree = updateTree.getTree();
        if (updateTreeNoMerge(updateTree, tree)) {
            treeRepository.merge(tree);
        }
        return tree;
    }

    public void validate(TreeFilter treeFilter, SecurityContext securityContext) {
        baseclassNewService.validateFilter(treeFilter,securityContext);

    }

    public void validate(TreeCreate treeCreate, SecurityContext securityContext) {
        baseclassNewService.validate(treeCreate,securityContext);
        TreeNode root = treeCreate.getRootId() != null ? getByIdOrNull(treeCreate.getRootId(), TreeNode.class, null, securityContext) : null;
        if (root == null && treeCreate.getRootId() != null) {
            throw new BadRequestException("No Tree Node With id " + treeCreate.getRootId());
        }
        treeCreate.setRoot(root);

    }
}
