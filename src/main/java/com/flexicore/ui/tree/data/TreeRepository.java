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
public class TreeRepository extends AbstractRepositoryPlugin implements ServicePlugin {


    public List<Tree> getAllTrees(TreeFilter treeFilter, SecurityContext securityContext) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<Tree> q=cb.createQuery(Tree.class);
        Root<Tree> r=q.from(Tree.class);
        List<Predicate> preds=new ArrayList<>();
        QueryInformationHolder<Tree> queryInformationHolder=new QueryInformationHolder<>(treeFilter,Tree.class,securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);

    }

    public long countAllTrees(TreeFilter treeFilter, SecurityContext securityContext) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<Long> q=cb.createQuery(Long.class);
        Root<Tree> r=q.from(Tree.class);
        List<Predicate> preds=new ArrayList<>();
        QueryInformationHolder<Tree> queryInformationHolder=new QueryInformationHolder<>(treeFilter,Tree.class,securityContext);
        return countAllFiltered(queryInformationHolder,preds,cb,q,r);

    }


}
