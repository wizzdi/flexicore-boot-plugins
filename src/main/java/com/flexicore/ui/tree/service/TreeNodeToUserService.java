package com.flexicore.ui.tree.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.User;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.tree.data.TreeNodeToUserRepository;
import com.flexicore.ui.tree.model.Tree;
import com.flexicore.ui.tree.model.TreeNode;
import com.flexicore.ui.tree.model.TreeNodeToUser;
import com.flexicore.ui.tree.request.*;
import com.flexicore.ui.tree.response.SaveTreeNodeStatusResponse;
import com.flexicore.ui.tree.response.TreeNodeStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@PluginInfo(version = 1)
@Component
public class TreeNodeToUserService implements ServicePlugin {

    @Autowired
    @PluginInfo(version = 1)

    private TreeNodeToUserRepository treeNodeToUserRepository;


    public SaveTreeNodeStatusResponse saveTreeNodeStatus(SaveTreeNodeStatusRequest saveTreeNodeStatusRequest, SecurityContext securityContext) {
        Map<String, TreeNodeToUser> links= treeNodeToUserRepository.getAllTreeNodeToUserLinks(saveTreeNodeStatusRequest.getNodeIdtoTree().keySet(),securityContext).parallelStream().collect(Collectors.toMap(f->f.getLeftside().getId(), f->f));
        List<Object> toMerge=new ArrayList<>();
        SaveTreeNodeStatusResponse saveTreeNodeStatusResponse=new SaveTreeNodeStatusResponse();
        for (Map.Entry<String, Boolean> entry : saveTreeNodeStatusRequest.getNodeStatus().entrySet()) {
            TreeNodeToUser link=links.get(entry.getKey());
            TreeNode treeNode=saveTreeNodeStatusRequest.getNodeIdtoTree().get(entry.getKey());
            if(link==null){
                link=createTreeNodeToTenantLinkNoMerge(treeNode,securityContext.getUser(),entry.getValue(),securityContext);
                toMerge.add(link);
                saveTreeNodeStatusResponse.setCreated(saveTreeNodeStatusResponse.getCreated()+1);

            }
            else{
                if(link.isNodeOpen()!=entry.getValue()){
                    link.setNodeOpen(entry.getValue());
                    toMerge.add(link);
                    saveTreeNodeStatusResponse.setUpdated(saveTreeNodeStatusResponse.getUpdated()+1);

                }
            }
        }
        treeNodeToUserRepository.massMerge(toMerge);
        return saveTreeNodeStatusResponse;

    }

    public TreeNodeToUser createTreeNodeToTenantLinkNoMerge(TreeNode treeNode, User user, Boolean value,SecurityContext securityContext) {
        TreeNodeToUser link=new TreeNodeToUser("link",securityContext);
        link.setTreeNode(treeNode);
        link.setUser(user);
        return link;
    }


    public TreeNodeStatusResponse getTreeNodeStatus(TreeNodeStatusRequest treeNodeStatusRequest, SecurityContext securityContext) {
        Map<String,Boolean> links= treeNodeToUserRepository.getAllTreeNodeToUserLinks(treeNodeStatusRequest.getNodeIds(),securityContext).parallelStream().collect(Collectors.toMap(f->f.getLeftside().getId(), f->f.isNodeOpen()));
        Map<String,Boolean> res=treeNodeStatusRequest.getNodeIds().parallelStream().collect(Collectors.toMap(f->f,f->links.getOrDefault(f,false)));
        return new TreeNodeStatusResponse(res);

    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return treeNodeToUserRepository.listByIds(c, ids, securityContext);
    }

    public void validate(SaveTreeNodeStatusRequest saveTreeNodeStatusRequest, SecurityContext securityContext) {
        if(saveTreeNodeStatusRequest.getNodeStatus().isEmpty()){
            throw new BadRequestException("No status was provided");
        }
        Set<String> ids=saveTreeNodeStatusRequest.getNodeIdtoTree().keySet();
        List<TreeNode> treeNodes=listByIds(TreeNode.class,ids,securityContext);
        ids.removeAll(treeNodes.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
        if(!ids.isEmpty()){
            throw new BadRequestException("No Tree nodes with ids " +ids);
        }
        Map<String, TreeNode> treeNodeMap = treeNodes.parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        saveTreeNodeStatusRequest.setNodeIdtoTree(treeNodeMap);
    }

    public void validate(TreeNodeStatusRequest treeNodeStatusRequest, SecurityContext securityContext) {
        if(treeNodeStatusRequest.getNodeIds().isEmpty()){
            throw new BadRequestException("Tree Nodes must be provided");
        }
        List<TreeNode> treeNodes=listByIds(TreeNode.class,treeNodeStatusRequest.getNodeIds(),securityContext);
        treeNodeStatusRequest.getNodeIds().removeAll(treeNodes.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
        if(!treeNodeStatusRequest.getNodeIds().isEmpty()){
            throw new BadRequestException("No Tree nodes with ids " + treeNodeStatusRequest.getNodeIds());
        }
        treeNodeStatusRequest.setTreeNodes(treeNodes);
    }


}
