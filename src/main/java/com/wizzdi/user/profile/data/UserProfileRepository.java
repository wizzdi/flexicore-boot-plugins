package com.wizzdi.user.profile.data;

import com.flexicore.model.Basic;
import com.flexicore.model.SecurityUser;
import com.flexicore.model.SecurityUser_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.user.profile.model.UserProfile;
import com.wizzdi.user.profile.model.UserProfile_;
import com.wizzdi.user.profile.request.UserProfileFilter;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Extension
@Component
public class UserProfileRepository implements Plugin {

    @Autowired
    private BasicRepository basicRepository;

    @PersistenceContext
    private EntityManager em;


    public List<UserProfile> listAllUserProfiles(
            UserProfileFilter userProfileFilter,
            SecurityContextBase securityContextBase) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserProfile> q = cb.createQuery(UserProfile.class);
        Root<UserProfile> r = q.from(UserProfile.class);
        List<Predicate> preds = new ArrayList<>();
        addUserProfilesPredicates(userProfileFilter, r, cb, q,preds);
        q.select(r).where(preds.toArray(Predicate[]::new)).orderBy(cb.desc(r.get(UserProfile_.creationDate)));
        TypedQuery<UserProfile> query=em.createQuery(q);
        BasicRepository.addPagination(userProfileFilter,query);
        return query.getResultList();
    }

    public <T extends UserProfile> void addUserProfilesPredicates(
            UserProfileFilter userProfileFilter, From<?,T> r,
            CriteriaBuilder cb,CommonAbstractCriteria q, List<Predicate> preds) {
        if (userProfileFilter.getUsers() != null&&!userProfileFilter.getUsers().isEmpty()) {
            Set<String> ids=userProfileFilter.getUsers().stream().map(f->f.getId()).collect(Collectors.toSet());
            Join<T, SecurityUser> join=r.join(UserProfile_.securityUser);
            preds.add(join.get(SecurityUser_.id).in(ids));
        }
    }

    public long countAllUserProfiles(UserProfileFilter userProfileFilter,
                                 SecurityContextBase securityContextBase) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<UserProfile> r = q.from(UserProfile.class);
        List<Predicate> preds = new ArrayList<>();
        addUserProfilesPredicates(userProfileFilter, r, cb,q, preds);
        q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
        TypedQuery<Long> query=em.createQuery(q);
        return query.getSingleResult();
    }


    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return basicRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return basicRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return basicRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        basicRepository.merge(base);
    }

    @Transactional
    public void merge(Object base, boolean updateDate) {
        basicRepository.merge(base, updateDate);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        basicRepository.massMerge(toMerge);
    }

    @Transactional
    public void massMerge(List<?> toMerge, boolean updatedate) {
        basicRepository.massMerge(toMerge, updatedate);
    }
}
