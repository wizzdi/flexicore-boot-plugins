package com.flexicore.ui.tree.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baselink_;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.tree.model.*;
import com.flexicore.ui.tree.request.TreeFilter;
import com.flexicore.ui.tree.request.TreeNodeFilter;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@PluginInfo(version = 1)
public class TreeNodeRepository extends AbstractRepositoryPlugin implements ServicePlugin {



    public List<TreeNode> getAllTreeNodes(TreeNodeFilter treeFilter, SecurityContext securityContext) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<TreeNode> q=cb.createQuery(TreeNode.class);
        Root<TreeNode> r=q.from(TreeNode.class);
        List<Predicate> preds=new ArrayList<>();
        addTreeNodeFiltering(r,cb,preds,treeFilter);
        QueryInformationHolder<TreeNode> queryInformationHolder=new QueryInformationHolder<>(treeFilter,TreeNode.class,securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);

    }

    public long countAllTreeNodes(TreeNodeFilter treeFilter, SecurityContext securityContext) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<Long> q=cb.createQuery(Long.class);
        Root<TreeNode> r=q.from(TreeNode.class);
        List<Predicate> preds=new ArrayList<>();
        addTreeNodeFiltering(r,cb,preds,treeFilter);
        QueryInformationHolder<TreeNode> queryInformationHolder=new QueryInformationHolder<>(treeFilter,TreeNode.class,securityContext);
        return countAllFiltered(queryInformationHolder,preds,cb,q,r);

    }

    private void addTreeNodeFiltering(Root<TreeNode> r, CriteriaBuilder cb, List<Predicate> preds,TreeNodeFilter treeNodeFilter) {
        if(treeNodeFilter.getParents()!=null && !treeNodeFilter.getParents().isEmpty()){
            Set<String> ids=treeNodeFilter.getParents().stream().map(f->f.getId()).collect(Collectors.toSet());
            preds.add(r.get(TreeNode_.parent).in(ids));
        }
    }

}
