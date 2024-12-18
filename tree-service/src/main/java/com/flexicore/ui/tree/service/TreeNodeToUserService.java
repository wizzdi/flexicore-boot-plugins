package com.flexicore.ui.tree.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecurityUser;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.ui.tree.data.TreeNodeToUserRepository;
import com.flexicore.ui.tree.model.TreeNode;
import com.flexicore.ui.tree.model.TreeNodeToUser;
import com.flexicore.ui.tree.model.TreeNode_;
import com.flexicore.ui.tree.request.SaveTreeNodeStatusRequest;
import com.flexicore.ui.tree.request.TreeNodeStatusRequest;
import com.flexicore.ui.tree.response.SaveTreeNodeStatusResponse;
import com.flexicore.ui.tree.response.TreeNodeStatusResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.service.BaseclassService;
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
public class TreeNodeToUserService implements Plugin {

    @Autowired
    private TreeNodeToUserRepository treeNodeToUserRepository;


    public SaveTreeNodeStatusResponse saveTreeNodeStatus(SaveTreeNodeStatusRequest saveTreeNodeStatusRequest, SecurityContext securityContext) {
        Map<String, TreeNodeToUser> links = treeNodeToUserRepository.getAllTreeNodeToUserLinks(saveTreeNodeStatusRequest.getNodeIdtoTree().keySet(), securityContext).parallelStream().collect(Collectors.toMap(f -> f.getTreeNode().getId(), f -> f));
        List<Object> toMerge = new ArrayList<>();
        SaveTreeNodeStatusResponse saveTreeNodeStatusResponse = new SaveTreeNodeStatusResponse();
        for (Map.Entry<String, Boolean> entry : saveTreeNodeStatusRequest.getNodeStatus().entrySet()) {
            TreeNodeToUser link = links.get(entry.getKey());
            TreeNode treeNode = saveTreeNodeStatusRequest.getNodeIdtoTree().get(entry.getKey());
            if (link == null) {
                link = createTreeNodeToTenantLinkNoMerge(treeNode, securityContext.getUser(), entry.getValue(), securityContext);
                toMerge.add(link);
                saveTreeNodeStatusResponse.setCreated(saveTreeNodeStatusResponse.getCreated() + 1);

            } else {
                if (link.isNodeOpen() != entry.getValue()) {
                    link.setNodeOpen(entry.getValue());
                    toMerge.add(link);
                    saveTreeNodeStatusResponse.setUpdated(saveTreeNodeStatusResponse.getUpdated() + 1);

                }
            }
        }
        treeNodeToUserRepository.massMerge(toMerge);
        return saveTreeNodeStatusResponse;

    }

    public TreeNodeToUser createTreeNodeToTenantLinkNoMerge(TreeNode treeNode, SecurityUser user, Boolean value, SecurityContext securityContext) {
        TreeNodeToUser link = new TreeNodeToUser();
        link.setId(UUID.randomUUID().toString());
        link.setTreeNode(treeNode);
        link.setUser(user);
        BaseclassService.createSecurityObjectNoMerge(link, securityContext);
        return link;
    }


    public TreeNodeStatusResponse getTreeNodeStatus(TreeNodeStatusRequest treeNodeStatusRequest, SecurityContext securityContext) {
        Map<String, Boolean> links = treeNodeStatusRequest.getTreeNodes().isEmpty()?new HashMap<>():treeNodeToUserRepository.getAllTreeNodeToUserLinks(treeNodeStatusRequest.getTreeNodes().stream().map(f->f.getId()).collect(Collectors.toSet()), securityContext).parallelStream().collect(Collectors.toMap(f -> f.getTreeNode().getId(), f -> f.isNodeOpen()));
        Map<String, Boolean> res = treeNodeStatusRequest.getTreeNodes().parallelStream().map(f->f.getId()).collect(Collectors.toMap(f -> f, f -> links.getOrDefault(f, false)));
        return new TreeNodeStatusResponse(res);

    }


    public void validate(SaveTreeNodeStatusRequest saveTreeNodeStatusRequest, SecurityContext securityContext) {
        if (saveTreeNodeStatusRequest.getNodeStatus().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No status was provided");
        }
        Set<String> ids = new HashSet<>(saveTreeNodeStatusRequest.getNodeStatus().keySet());
        List<TreeNode> treeNodes = listByIds(TreeNode.class, ids,securityContext);
        ids.removeAll(treeNodes.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
        if (!ids.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Tree nodes with ids " + ids);
        }
        Map<String, TreeNode> treeNodeMap = treeNodes.parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        saveTreeNodeStatusRequest.setNodeIdtoTree(treeNodeMap);
    }

    public void validate(TreeNodeStatusRequest treeNodeStatusRequest, SecurityContext securityContext) {
        if (treeNodeStatusRequest.getNodeIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tree Nodes must be provided");
        }
        List<TreeNode> treeNodes = listByIds(TreeNode.class, treeNodeStatusRequest.getNodeIds(),securityContext);
        treeNodeStatusRequest.getNodeIds().removeAll(treeNodes.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
        if (!treeNodeStatusRequest.getNodeIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Tree nodes with ids " + treeNodeStatusRequest.getNodeIds());
        }
        treeNodeStatusRequest.setTreeNodes(treeNodes);
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return treeNodeToUserRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return treeNodeToUserRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return treeNodeToUserRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return treeNodeToUserRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return treeNodeToUserRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return treeNodeToUserRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return treeNodeToUserRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        treeNodeToUserRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        treeNodeToUserRepository.massMerge(toMerge);
    }
}
