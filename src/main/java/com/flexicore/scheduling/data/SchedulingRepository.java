package com.flexicore.scheduling.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.QueryInformationHolder;
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
            preds.add(cb.equal(join.get(ScheduleToAction_.rightside), filtering.getSchedule()));
        }
        QueryInformationHolder<ScheduleAction> queryInformationHolder = new QueryInformationHolder<>(filtering, ScheduleAction.class, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);
    }
}
