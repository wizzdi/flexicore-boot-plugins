package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass_;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.DeviceType_;
import com.wizzdi.basic.iot.model.Device_;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.model.RemoteGroupMembershipSource;
import com.wizzdi.basic.iot.model.RemoteGroupPopulationType;
import com.wizzdi.basic.iot.model.RemoteGroupToRemote;
import com.wizzdi.basic.iot.model.RemoteGroupToRemote_;
import com.wizzdi.basic.iot.model.RemoteGroup_;
import com.wizzdi.basic.iot.model.Remote_;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Extension
@Component
public class DeviceTypeRemoteGroupRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public DeviceType findDeviceType(String id) {
        return securedBasicRepository.findByIdOrNull(DeviceType.class, id);
    }

    public Device findDevice(String id) {
        return securedBasicRepository.findByIdOrNull(Device.class, id);
    }

    public RemoteGroup findManagedGroupByDeviceType(String deviceTypeId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroup> q = cb.createQuery(RemoteGroup.class);
        Root<RemoteGroup> r = q.from(RemoteGroup.class);
        q.select(r).where(
                cb.isTrue(r.get(RemoteGroup_.systemManaged)),
                cb.equal(r.get(RemoteGroup_.populationType), RemoteGroupPopulationType.DEVICE_TYPE),
                cb.equal(r.get(RemoteGroup_.sourceDeviceType).get(DeviceType_.id), deviceTypeId));
        List<RemoteGroup> result = em.createQuery(q).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.getFirst();
    }

    public List<String> listDeviceTypeIdsAfter(String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> q = cb.createQuery(String.class);
        Root<DeviceType> r = q.from(DeviceType.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isFalse(r.get(Baseclass_.softDelete)));
        if (afterId != null) {
            predicates.add(cb.greaterThan(r.get(DeviceType_.id), afterId));
        }
        q.select(r.get(DeviceType_.id))
                .where(predicates.toArray(Predicate[]::new))
                .orderBy(cb.asc(r.get(DeviceType_.id)));
        return em.createQuery(q).setMaxResults(limit).getResultList();
    }

    public List<String> listDeviceIdsAfter(String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> q = cb.createQuery(String.class);
        Root<Device> r = q.from(Device.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isFalse(r.get(Baseclass_.softDelete)));
        if (afterId != null) {
            predicates.add(cb.greaterThan(r.get(Device_.id), afterId));
        }
        q.select(r.get(Device_.id))
                .where(predicates.toArray(Predicate[]::new))
                .orderBy(cb.asc(r.get(Device_.id)));
        return em.createQuery(q).setMaxResults(limit).getResultList();
    }

    public List<RemoteGroupToRemote> listAutomaticMembershipsForRemote(String remoteId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupToRemote> q = cb.createQuery(RemoteGroupToRemote.class);
        Root<RemoteGroupToRemote> r = q.from(RemoteGroupToRemote.class);
        q.select(r).where(
                cb.equal(r.get(RemoteGroupToRemote_.remote).get(Remote_.id), remoteId),
                cb.equal(r.get(RemoteGroupToRemote_.membershipSource), RemoteGroupMembershipSource.DEVICE_TYPE));
        return em.createQuery(q).getResultList();
    }

    public RemoteGroupToRemote findAutomaticMembership(String groupId, String remoteId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupToRemote> q = cb.createQuery(RemoteGroupToRemote.class);
        Root<RemoteGroupToRemote> r = q.from(RemoteGroupToRemote.class);
        q.select(r).where(
                cb.equal(r.get(RemoteGroupToRemote_.remoteGroup).get(RemoteGroup_.id), groupId),
                cb.equal(r.get(RemoteGroupToRemote_.remote).get(Remote_.id), remoteId),
                cb.equal(r.get(RemoteGroupToRemote_.membershipSource), RemoteGroupMembershipSource.DEVICE_TYPE));
        List<RemoteGroupToRemote> result = em.createQuery(q).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.getFirst();
    }

    @Transactional
    public void merge(Object entity) {
        securedBasicRepository.merge(entity);
    }

    @Transactional
    public void massMerge(List<?> entities) {
        if (entities != null && !entities.isEmpty()) {
            securedBasicRepository.massMerge(entities);
        }
    }
}
