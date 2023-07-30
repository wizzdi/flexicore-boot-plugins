package com.flexicore.ui.tree.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.tree.model.TreeNode;
import com.flexicore.ui.tree.model.TreeNode_;
import com.flexicore.ui.tree.request.TreeNodeFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@Extension
public class TreeNodeRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;


    public List<TreeNode> getAllTreeNodes(TreeNodeFilter treeFilter, SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TreeNode> q = cb.createQuery(TreeNode.class);
        Root<TreeNode> r = q.from(TreeNode.class);
        List<Predicate> preds = new ArrayList<>();
        addTreeNodeFiltering(cb, q, r, preds, treeFilter, securityContext);
        q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<TreeNode> query = em.createQuery(q);
        BasicRepository.addPagination(treeFilter, query);
        return query.getResultList();


    }

    public long countAllTreeNodes(TreeNodeFilter treeFilter, SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<TreeNode> r = q.from(TreeNode.class);
        List<Predicate> preds = new ArrayList<>();
        addTreeNodeFiltering(cb, q, r, preds, treeFilter, securityContext);
        q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();

    }

    public <T extends TreeNode> void addTreeNodeFiltering(CriteriaBuilder cb, CommonAbstractCriteria q, Root<TreeNode> r, List<Predicate> preds, TreeNodeFilter treeNodeFilter, SecurityContextBase securityContextBase) {
        securedBasicRepository.addSecuredBasicPredicates(treeNodeFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContextBase);
        if (treeNodeFilter.getParents() != null && !treeNodeFilter.getParents().isEmpty()) {
            Set<String> ids = treeNodeFilter.getParents().stream().map(f -> f.getId()).collect(Collectors.toSet());
            preds.add(r.get(TreeNode_.parent).in(ids));
        }
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return securedBasicRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return securedBasicRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return securedBasicRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return securedBasicRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return securedBasicRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return securedBasicRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        securedBasicRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        securedBasicRepository.massMerge(toMerge);
    }
}
