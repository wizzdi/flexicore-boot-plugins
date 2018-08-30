package com.flexicore.scheduling.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baselink_;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.scheduling.containers.request.LinkScheduleToAction;
import com.flexicore.scheduling.containers.request.SchedulingActionFiltering;
import com.flexicore.scheduling.model.ScheduleAction;
import com.flexicore.scheduling.model.ScheduleAction_;
import com.flexicore.scheduling.model.ScheduleToAction;
import com.flexicore.scheduling.model.ScheduleToAction_;
import com.flexicore.security.SecurityContext;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@PluginInfo(version = 1)
public class SchedulingRepository extends AbstractRepositoryPlugin {

    public List<ScheduleAction> getAllScheduleActions(SchedulingActionFiltering filtering, SecurityContext securityContext) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ScheduleAction> q = cb.createQuery(ScheduleAction.class);
        Root<ScheduleAction> r = q.from(ScheduleAction.class);
        List<Predicate> preds = new ArrayList<>();
        if (filtering.getSchedule() != null) {
            Join<ScheduleAction, ScheduleToAction> join = r.join(ScheduleAction_.scheduleToActions);
            preds.add(cb.and(cb.equal(join.get(Baselink_.leftside), filtering.getSchedule()),cb.not(cb.isTrue(join.get(ScheduleToAction_.softDelete)))));
        }
        QueryInformationHolder<ScheduleAction> queryInformationHolder = new QueryInformationHolder<>(filtering, ScheduleAction.class, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);
    }

    public List<ScheduleToAction> getAllSchedullingLinks(LinkScheduleToAction linkScheduleToAction, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ScheduleToAction> q = cb.createQuery(ScheduleToAction.class);
        Root<ScheduleToAction> r = q.from(ScheduleToAction.class);
        List<Predicate> preds = new ArrayList<>();
        preds.add(cb.and(cb.equal(r.get(Baselink_.leftside),linkScheduleToAction.getSchedule()),cb.equal(r.get(Baselink_.rightside),linkScheduleToAction.getScheduleAction())));
        QueryInformationHolder<ScheduleToAction> queryInformationHolder = new QueryInformationHolder<>(new FilteringInformationHolder(), ScheduleToAction.class, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);
    }
}
