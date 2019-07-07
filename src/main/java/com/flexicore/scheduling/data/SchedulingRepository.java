package com.flexicore.scheduling.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.Baselink_;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.scheduling.containers.request.LinkScheduleToAction;
import com.flexicore.scheduling.containers.request.SchedulingActionFiltering;
import com.flexicore.scheduling.containers.request.SchedulingTimeslotFiltering;
import com.flexicore.scheduling.model.*;
import com.flexicore.security.SecurityContext;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@PluginInfo(version = 1)
@Transactional
public class SchedulingRepository extends AbstractRepositoryPlugin {

    public List<ScheduleAction> getAllScheduleActions(SchedulingActionFiltering filtering, SecurityContext securityContext) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ScheduleAction> q = cb.createQuery(ScheduleAction.class);
        Root<ScheduleAction> r = q.from(ScheduleAction.class);
        List<Predicate> preds = new ArrayList<>();
        if (filtering.getSchedule() != null) {
            Join<ScheduleAction, ScheduleToAction> join = r.join(ScheduleAction_.scheduleToActions);
            preds.add(cb.and(cb.equal(join.get(Baselink_.leftside), filtering.getSchedule()), cb.not(cb.isTrue(join.get(ScheduleToAction_.softDelete)))));
        }
        QueryInformationHolder<ScheduleAction> queryInformationHolder = new QueryInformationHolder<>(filtering, ScheduleAction.class, securityContext);

        return getAllFiltered(queryInformationHolder, preds, cb, q, r);
    }

    public List<ScheduleToAction> getAllSchedullingLinks(LinkScheduleToAction linkScheduleToAction, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ScheduleToAction> q = cb.createQuery(ScheduleToAction.class);
        Root<ScheduleToAction> r = q.from(ScheduleToAction.class);
        List<Predicate> preds = new ArrayList<>();
        if( linkScheduleToAction.getSchedule()!=null){
            preds.add(cb.equal(r.get(Baselink_.leftside), linkScheduleToAction.getSchedule()));
        }
        if(linkScheduleToAction.getScheduleAction()!=null){
            preds.add(cb.equal(r.get(Baselink_.rightside), linkScheduleToAction.getScheduleAction()));
        }
        QueryInformationHolder<ScheduleToAction> queryInformationHolder = new QueryInformationHolder<>(new FilteringInformationHolder(), ScheduleToAction.class, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);
    }

    public List<ScheduleTimeslot> getAllTimeSlots(Set<String> scheduleIds, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ScheduleTimeslot> q = cb.createQuery(ScheduleTimeslot.class);
        Root<ScheduleTimeslot> r = q.from(ScheduleTimeslot.class);
        Join<ScheduleTimeslot, Schedule> join = r.join(ScheduleTimeslot_.schedule);
        List<Predicate> preds = new ArrayList<>();
        preds.add(join.get(Schedule_.id).in(scheduleIds));
        QueryInformationHolder<ScheduleTimeslot> queryInformationHolder = new QueryInformationHolder<>(new FilteringInformationHolder(), ScheduleTimeslot.class, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);
    }

    public List<ScheduleTimeslot> getAllScheduleTimeslots(SchedulingTimeslotFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ScheduleTimeslot> q = cb.createQuery(ScheduleTimeslot.class);
        Root<ScheduleTimeslot> r = q.from(ScheduleTimeslot.class);
        List<Predicate> preds = new ArrayList<>();
        preds.add(cb.equal(r.get(ScheduleTimeslot_.schedule), filtering.getSchedule()));
        QueryInformationHolder<ScheduleTimeslot> queryInformationHolder = new QueryInformationHolder<>(new FilteringInformationHolder(), ScheduleTimeslot.class, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void merge(Object base) {
        super.merge(base);
    }
}
