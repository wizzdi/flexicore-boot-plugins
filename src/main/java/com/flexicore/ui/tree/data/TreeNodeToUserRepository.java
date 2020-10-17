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
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@Extension
@PluginInfo(version = 1)
public class TreeNodeToUserRepository extends AbstractRepositoryPlugin implements ServicePlugin {


    public List<TreeNodeToUser> getAllTreeNodeToUserLinks(Set<String> ids, SecurityContext securityContext) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<TreeNodeToUser> q=cb.createQuery(TreeNodeToUser.class);
        Root<TreeNodeToUser> r=q.from(TreeNodeToUser.class);
        List<Predicate> preds=new ArrayList<>();
        Join<TreeNodeToUser,TreeNode> join=r.join(TreeNodeToUser_.treeNode);
        preds.add(cb.and(
                cb.not(cb.isTrue(r.get(Baselink_.softDelete))),
                join.get(TreeNode_.id).in(ids),
                cb.equal(r.get(TreeNodeToUser_.user),securityContext.getUser())
        ));
        QueryInformationHolder<TreeNodeToUser> queryInformationHolder=new QueryInformationHolder<>(new FilteringInformationHolder(),TreeNodeToUser.class,securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);
    }
}
