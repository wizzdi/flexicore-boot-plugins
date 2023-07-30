package com.wizzdi.messaging.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BaseclassRepository;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.SoftDeleteOption;
import com.wizzdi.messaging.model.*;
import com.wizzdi.messaging.request.ChatToChatUserFilter;
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
public class ChatToChatUserRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private BaseclassRepository baseclassRepository;
    @Autowired
    private BasicRepository basicRepository;


    public List<ChatToChatUser> listAllChatToChatUsers(ChatToChatUserFilter ChatToChatUserFilter, SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ChatToChatUser> q = cb.createQuery(ChatToChatUser.class);
        Root<ChatToChatUser> r = q.from(ChatToChatUser.class);
        List<Predicate> predicates = new ArrayList<>();
        addChatToChatUserPredicates(ChatToChatUserFilter, cb, q, r, predicates, securityContext);
        q.select(r).where(predicates.toArray(Predicate[]::new));
        TypedQuery<ChatToChatUser> query = em.createQuery(q);
        BasicRepository.addPagination(ChatToChatUserFilter, query);
        return query.getResultList();

    }

    public <T extends ChatToChatUser> void addChatToChatUserPredicates(ChatToChatUserFilter chatToChatUserFilter, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> predicates, SecurityContextBase securityContext) {
        if (chatToChatUserFilter.getBasicPropertiesFilter() == null) {
            chatToChatUserFilter.setBasicPropertiesFilter(new BasicPropertiesFilter().setSoftDelete(SoftDeleteOption.NON_DELETED_ONLY));
        }
        BasicRepository.addBasicPropertiesFilter(chatToChatUserFilter.getBasicPropertiesFilter(), cb, q, r, predicates);

        if(chatToChatUserFilter.getDisabled()==null){
            chatToChatUserFilter.setDisabled(false);
        }
        predicates.add(cb.equal(r.get(ChatToChatUser_.disabled),chatToChatUserFilter.getDisabled()));

        if (chatToChatUserFilter.getChats() != null && !chatToChatUserFilter.getChats().isEmpty()) {
            Set<String> ids = chatToChatUserFilter.getChats().stream().map(f -> f.getId()).collect(Collectors.toSet());
            Join<T, Chat> join = r.join(ChatToChatUser_.chat);
            predicates.add(join.get(Chat_.id).in(ids));
        }

        if (chatToChatUserFilter.getChatUsers() != null && !chatToChatUserFilter.getChatUsers().isEmpty()) {
            Set<String> ids = chatToChatUserFilter.getChatUsers().stream().map(f -> f.getId()).collect(Collectors.toSet());
            Join<T, ChatUser> join = r.join(ChatToChatUser_.chatUser);
            predicates.add(join.get(ChatUser_.id).in(ids));
        }


    }

    public long countAllChatToChatUsers(ChatToChatUserFilter ChatToChatUserFilter, SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<ChatToChatUser> r = q.from(ChatToChatUser.class);
        List<Predicate> predicates = new ArrayList<>();
        addChatToChatUserPredicates(ChatToChatUserFilter, cb, q, r, predicates, securityContext);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();

    }

    @Transactional
    public void merge(Object base) {
        basicRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        basicRepository.massMerge(toMerge);
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return baseclassRepository.listByIds(c, ids, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return baseclassRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return baseclassRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return baseclassRepository.getByIdOrNull(id, c, securityContext);
    }

    public <T extends Baseclass> List<T> findByIds(Class<T> c, Set<String> requested) {
        return baseclassRepository.findByIds(c, requested);
    }


}
