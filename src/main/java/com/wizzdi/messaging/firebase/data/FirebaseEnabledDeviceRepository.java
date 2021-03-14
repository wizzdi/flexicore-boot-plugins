package com.wizzdi.messaging.firebase.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BaseclassRepository;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.messaging.connectors.firebase.model.FirebaseEnabledDevice;
import com.wizzdi.messaging.data.MessageReceiverDeviceRepository;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceFilter;
import com.wizzdi.messaging.model.*;
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

@Component
@Extension
public class FirebaseEnabledDeviceRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private BaseclassRepository baseclassRepository;
	@Autowired
	private MessageReceiverDeviceRepository messageReceiverDeviceRepository;


	public List<FirebaseEnabledDevice> listAllFirebaseEnabledDevices(FirebaseEnabledDeviceFilter FirebaseEnabledDeviceFilter, SecurityContextBase securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FirebaseEnabledDevice> q = cb.createQuery(FirebaseEnabledDevice.class);
		Root<FirebaseEnabledDevice> r = q.from(FirebaseEnabledDevice.class);
		List<Predicate> predicates = new ArrayList<>();
		addFirebaseEnabledDevicePredicates(FirebaseEnabledDeviceFilter, cb, q, r, predicates, securityContext);
		q.select(r).where(predicates.toArray(Predicate[]::new));
		TypedQuery<FirebaseEnabledDevice> query = em.createQuery(q);
		BasicRepository.addPagination(FirebaseEnabledDeviceFilter, query);
		return query.getResultList();

	}

	public <T extends FirebaseEnabledDevice> void addFirebaseEnabledDevicePredicates(FirebaseEnabledDeviceFilter firebaseEnabledDeviceFilter, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> predicates, SecurityContextBase securityContext) {

		messageReceiverDeviceRepository.addMessageReceiverDevicePredicates(firebaseEnabledDeviceFilter,cb,q,r,predicates,securityContext);


	}

	public long countAllFirebaseEnabledDevices(FirebaseEnabledDeviceFilter FirebaseEnabledDeviceFilter, SecurityContextBase securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<FirebaseEnabledDevice> r = q.from(FirebaseEnabledDevice.class);
		List<Predicate> predicates = new ArrayList<>();
		addFirebaseEnabledDevicePredicates(FirebaseEnabledDeviceFilter, cb, q, r, predicates, securityContext);
		q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();

	}

	@Transactional
	public void merge(Object o) {
		em.merge(o);
	}

	@Transactional
	public void massMerge(List<Object> list) {
		for (Object o : list) {
			em.merge(o);
		}
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return baseclassRepository.listByIds(c, ids, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return baseclassRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return baseclassRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return baseclassRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <T extends Baseclass> List<T> findByIds(Class<T> c, Set<String> requested) {
		return baseclassRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return baseclassRepository.findByIdOrNull(type, id);
	}
}
