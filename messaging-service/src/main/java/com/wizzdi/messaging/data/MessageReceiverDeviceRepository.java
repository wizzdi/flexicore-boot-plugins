package com.wizzdi.messaging.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.model.ChatUser_;
import com.wizzdi.messaging.model.MessageReceiverDevice;
import com.wizzdi.messaging.model.MessageReceiverDevice_;
import com.wizzdi.messaging.request.MessageReceiverDeviceFilter;
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
public class MessageReceiverDeviceRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private SecuredBasicRepository securedBasicRepository;



	public List<MessageReceiverDevice> listAllMessageReceiverDevices(MessageReceiverDeviceFilter MessageReceiverDeviceFilter, SecurityContextBase securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MessageReceiverDevice> q = cb.createQuery(MessageReceiverDevice.class);
		Root<MessageReceiverDevice> r = q.from(MessageReceiverDevice.class);
		List<Predicate> predicates = new ArrayList<>();
		addMessageReceiverDevicePredicates(MessageReceiverDeviceFilter, cb, q, r, predicates, securityContext);
		q.select(r).where(predicates.toArray(Predicate[]::new));
		TypedQuery<MessageReceiverDevice> query = em.createQuery(q);
		BasicRepository.addPagination(MessageReceiverDeviceFilter, query);
		return query.getResultList();

	}

	public <T extends MessageReceiverDevice> void addMessageReceiverDevicePredicates(MessageReceiverDeviceFilter messageReceiverDeviceFilter, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> predicates, SecurityContextBase securityContext) {

		securedBasicRepository.addSecuredBasicPredicates(messageReceiverDeviceFilter.getBasicPropertiesFilter(),cb,q,r,predicates,securityContext);

		if(messageReceiverDeviceFilter.getChatUsers()!=null&&!messageReceiverDeviceFilter.getChatUsers().isEmpty()){
			Set<String> ids=messageReceiverDeviceFilter.getChatUsers().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<T, ChatUser> join=r.join(MessageReceiverDevice_.owner);
			predicates.add(join.get(ChatUser_.id).in(ids));
		}

		if(messageReceiverDeviceFilter.getExternalIds()!=null&&!messageReceiverDeviceFilter.getExternalIds().isEmpty()){
			predicates.add(r.get(MessageReceiverDevice_.externalId).in(messageReceiverDeviceFilter.getExternalIds()));
		}


	}

	public long countAllMessageReceiverDevices(MessageReceiverDeviceFilter MessageReceiverDeviceFilter, SecurityContextBase securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<MessageReceiverDevice> r = q.from(MessageReceiverDevice.class);
		List<Predicate> predicates = new ArrayList<>();
		addMessageReceiverDevicePredicates(MessageReceiverDeviceFilter, cb, q, r, predicates, securityContext);
		q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();

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
