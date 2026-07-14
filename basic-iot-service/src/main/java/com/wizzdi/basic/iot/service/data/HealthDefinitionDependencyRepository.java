package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass_;
import com.wizzdi.basic.iot.model.HealthSignalDefinition_;
import com.wizzdi.basic.iot.model.HealthSignalMapping;
import com.wizzdi.basic.iot.model.HealthSignalMapping_;
import com.wizzdi.basic.iot.model.RemoteHealthProfile_;
import com.wizzdi.basic.iot.model.RemoteHealthRuleCondition;
import com.wizzdi.basic.iot.model.RemoteHealthRuleCondition_;
import com.wizzdi.basic.iot.model.RemoteHealthRule_;
import com.wizzdi.basic.iot.model.StatePropertyDefinition_;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Extension
@Component
public class HealthDefinitionDependencyRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;

    public Set<String> listProfileIdsByHealthSignalDefinition(String healthSignalDefinitionId) {
        Set<String> result = new LinkedHashSet<>();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<String> mappingQuery = cb.createQuery(String.class);
        Root<HealthSignalMapping> mapping = mappingQuery.from(HealthSignalMapping.class);
        mappingQuery.select(mapping.get(HealthSignalMapping_.remoteHealthProfile).get(RemoteHealthProfile_.id)).distinct(true).where(
                cb.equal(mapping.get(HealthSignalMapping_.healthSignal).get(HealthSignalDefinition_.id), healthSignalDefinitionId),
                cb.isFalse(mapping.get(Baseclass_.softDelete)),
                cb.isFalse(mapping.get(HealthSignalMapping_.remoteHealthProfile).get(Baseclass_.softDelete))
        );
        result.addAll(em.createQuery(mappingQuery).getResultList());

        CriteriaQuery<String> conditionQuery = cb.createQuery(String.class);
        Root<RemoteHealthRuleCondition> condition = conditionQuery.from(RemoteHealthRuleCondition.class);
        conditionQuery.select(condition.get(RemoteHealthRuleCondition_.remoteHealthRule)
                        .get(RemoteHealthRule_.remoteHealthProfile)
                        .get(RemoteHealthProfile_.id))
                .distinct(true)
                .where(
                        cb.equal(condition.get(RemoteHealthRuleCondition_.healthSignal)
                                .get(HealthSignalDefinition_.id), healthSignalDefinitionId),
                        cb.isFalse(condition.get(Baseclass_.softDelete)),
                        cb.isFalse(condition.get(RemoteHealthRuleCondition_.remoteHealthRule).get(Baseclass_.softDelete)),
                        cb.isFalse(condition.get(RemoteHealthRuleCondition_.remoteHealthRule)
                                .get(RemoteHealthRule_.remoteHealthProfile)
                                .get(Baseclass_.softDelete))
                );
        result.addAll(em.createQuery(conditionQuery).getResultList());
        return result;
    }

    public Set<String> listProfileIdsByStatePropertyDefinition(String statePropertyDefinitionId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<HealthSignalMapping> mapping = query.from(HealthSignalMapping.class);
        query.select(mapping.get(HealthSignalMapping_.remoteHealthProfile).get(RemoteHealthProfile_.id)).distinct(true).where(
                cb.equal(mapping.get(HealthSignalMapping_.stateProperty)
                        .get(StatePropertyDefinition_.id), statePropertyDefinitionId),
                cb.isFalse(mapping.get(Baseclass_.softDelete)),
                cb.isFalse(mapping.get(HealthSignalMapping_.remoteHealthProfile).get(Baseclass_.softDelete))
        );
        return new LinkedHashSet<>(em.createQuery(query).getResultList());
    }
}
