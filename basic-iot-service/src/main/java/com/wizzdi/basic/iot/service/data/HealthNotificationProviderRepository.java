package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass_;
import com.flexicore.model.SecurityTenant_;
import com.wizzdi.basic.iot.model.HealthNotificationDeliveryMode;
import com.wizzdi.basic.iot.model.HealthNotificationEventType;
import com.wizzdi.basic.iot.model.SendGridHealthNotificationConfiguration;
import com.wizzdi.basic.iot.model.SendGridHealthNotificationConfiguration_;
import com.wizzdi.basic.iot.model.SendGridHealthNotificationTemplate;
import com.wizzdi.basic.iot.model.SendGridHealthNotificationTemplate_;
import com.wizzdi.basic.iot.model.WhatsAppCloudHealthNotificationConfiguration;
import com.wizzdi.basic.iot.model.WhatsAppCloudHealthNotificationConfiguration_;
import com.wizzdi.basic.iot.model.WhatsAppHealthNotificationTemplate;
import com.wizzdi.basic.iot.model.WhatsAppHealthNotificationTemplateParameter;
import com.wizzdi.basic.iot.model.WhatsAppHealthNotificationTemplateParameter_;
import com.wizzdi.basic.iot.model.WhatsAppHealthNotificationTemplate_;
import com.wizzdi.basic.iot.service.request.HealthNotificationProviderConfigurationFilter;
import com.wizzdi.basic.iot.service.request.HealthNotificationProviderTemplateFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.data.BasicRepository;
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
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Extension
@Component
public class HealthNotificationProviderRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<SendGridHealthNotificationConfiguration> listSendGridConfigurations(
            SecurityContext securityContext,
            HealthNotificationProviderConfigurationFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SendGridHealthNotificationConfiguration> q = cb.createQuery(SendGridHealthNotificationConfiguration.class);
        Root<SendGridHealthNotificationConfiguration> r = q.from(SendGridHealthNotificationConfiguration.class);
        List<Predicate> predicates = new ArrayList<>();
        securedBasicRepository.addSecuredBasicPredicates(
                filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        addSendGridConfigurationPredicates(filter, cb, r, predicates);
        q.select(r).where(predicates.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(Baseclass_.name)));
        TypedQuery<SendGridHealthNotificationConfiguration> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long countSendGridConfigurations(SecurityContext securityContext,
                                            HealthNotificationProviderConfigurationFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<SendGridHealthNotificationConfiguration> r = q.from(SendGridHealthNotificationConfiguration.class);
        List<Predicate> predicates = new ArrayList<>();
        securedBasicRepository.addSecuredBasicPredicates(
                filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        addSendGridConfigurationPredicates(filter, cb, r, predicates);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    private void addSendGridConfigurationPredicates(
            HealthNotificationProviderConfigurationFilter filter,
            CriteriaBuilder cb,
            Root<SendGridHealthNotificationConfiguration> r,
            List<Predicate> predicates) {
        if (filter.getConfigurationIds() != null && !filter.getConfigurationIds().isEmpty()) {
            predicates.add(r.get(Baseclass_.id).in(filter.getConfigurationIds()));
        }
        if (filter.getEnabled() != null) {
            predicates.add(cb.equal(r.get(SendGridHealthNotificationConfiguration_.enabled), filter.getEnabled()));
        }
    }

    public List<WhatsAppCloudHealthNotificationConfiguration> listWhatsAppConfigurations(
            SecurityContext securityContext,
            HealthNotificationProviderConfigurationFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WhatsAppCloudHealthNotificationConfiguration> q = cb.createQuery(WhatsAppCloudHealthNotificationConfiguration.class);
        Root<WhatsAppCloudHealthNotificationConfiguration> r = q.from(WhatsAppCloudHealthNotificationConfiguration.class);
        List<Predicate> predicates = new ArrayList<>();
        securedBasicRepository.addSecuredBasicPredicates(
                filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        addWhatsAppConfigurationPredicates(filter, cb, r, predicates);
        q.select(r).where(predicates.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(Baseclass_.name)));
        TypedQuery<WhatsAppCloudHealthNotificationConfiguration> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long countWhatsAppConfigurations(SecurityContext securityContext,
                                            HealthNotificationProviderConfigurationFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<WhatsAppCloudHealthNotificationConfiguration> r = q.from(WhatsAppCloudHealthNotificationConfiguration.class);
        List<Predicate> predicates = new ArrayList<>();
        securedBasicRepository.addSecuredBasicPredicates(
                filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        addWhatsAppConfigurationPredicates(filter, cb, r, predicates);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    private void addWhatsAppConfigurationPredicates(
            HealthNotificationProviderConfigurationFilter filter,
            CriteriaBuilder cb,
            Root<WhatsAppCloudHealthNotificationConfiguration> r,
            List<Predicate> predicates) {
        if (filter.getConfigurationIds() != null && !filter.getConfigurationIds().isEmpty()) {
            predicates.add(r.get(Baseclass_.id).in(filter.getConfigurationIds()));
        }
        if (filter.getEnabled() != null) {
            predicates.add(cb.equal(r.get(WhatsAppCloudHealthNotificationConfiguration_.enabled), filter.getEnabled()));
        }
    }

    public List<SendGridHealthNotificationTemplate> listSendGridTemplates(
            SecurityContext securityContext,
            HealthNotificationProviderTemplateFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SendGridHealthNotificationTemplate> q = cb.createQuery(SendGridHealthNotificationTemplate.class);
        Root<SendGridHealthNotificationTemplate> r = q.from(SendGridHealthNotificationTemplate.class);
        List<Predicate> predicates = new ArrayList<>();
        securedBasicRepository.addSecuredBasicPredicates(
                filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        addTemplatePredicates(filter, cb, predicates,
                r.get(Baseclass_.id),
                r.get(SendGridHealthNotificationTemplate_.sendGridConfiguration).get(Baseclass_.id),
                r.get(SendGridHealthNotificationTemplate_.eventType),
                r.get(SendGridHealthNotificationTemplate_.deliveryMode),
                r.get(SendGridHealthNotificationTemplate_.locale),
                r.get(SendGridHealthNotificationTemplate_.enabled));
        q.select(r).where(predicates.toArray(Predicate[]::new))
                .orderBy(cb.desc(r.get(SendGridHealthNotificationTemplate_.priority)), cb.asc(r.get(Baseclass_.name)));
        TypedQuery<SendGridHealthNotificationTemplate> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long countSendGridTemplates(SecurityContext securityContext,
                                       HealthNotificationProviderTemplateFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<SendGridHealthNotificationTemplate> r = q.from(SendGridHealthNotificationTemplate.class);
        List<Predicate> predicates = new ArrayList<>();
        securedBasicRepository.addSecuredBasicPredicates(
                filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        addTemplatePredicates(filter, cb, predicates,
                r.get(Baseclass_.id),
                r.get(SendGridHealthNotificationTemplate_.sendGridConfiguration).get(Baseclass_.id),
                r.get(SendGridHealthNotificationTemplate_.eventType),
                r.get(SendGridHealthNotificationTemplate_.deliveryMode),
                r.get(SendGridHealthNotificationTemplate_.locale),
                r.get(SendGridHealthNotificationTemplate_.enabled));
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    public List<WhatsAppHealthNotificationTemplate> listWhatsAppTemplates(
            SecurityContext securityContext,
            HealthNotificationProviderTemplateFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WhatsAppHealthNotificationTemplate> q = cb.createQuery(WhatsAppHealthNotificationTemplate.class);
        Root<WhatsAppHealthNotificationTemplate> r = q.from(WhatsAppHealthNotificationTemplate.class);
        List<Predicate> predicates = new ArrayList<>();
        securedBasicRepository.addSecuredBasicPredicates(
                filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        addTemplatePredicates(filter, cb, predicates,
                r.get(Baseclass_.id),
                r.get(WhatsAppHealthNotificationTemplate_.whatsAppConfiguration).get(Baseclass_.id),
                r.get(WhatsAppHealthNotificationTemplate_.eventType),
                r.get(WhatsAppHealthNotificationTemplate_.deliveryMode),
                r.get(WhatsAppHealthNotificationTemplate_.locale),
                r.get(WhatsAppHealthNotificationTemplate_.enabled));
        q.select(r).where(predicates.toArray(Predicate[]::new))
                .orderBy(cb.desc(r.get(WhatsAppHealthNotificationTemplate_.priority)), cb.asc(r.get(Baseclass_.name)));
        TypedQuery<WhatsAppHealthNotificationTemplate> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        List<WhatsAppHealthNotificationTemplate> result = query.getResultList();
        populateWhatsAppParameters(result);
        return result;
    }

    public long countWhatsAppTemplates(SecurityContext securityContext,
                                       HealthNotificationProviderTemplateFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<WhatsAppHealthNotificationTemplate> r = q.from(WhatsAppHealthNotificationTemplate.class);
        List<Predicate> predicates = new ArrayList<>();
        securedBasicRepository.addSecuredBasicPredicates(
                filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        addTemplatePredicates(filter, cb, predicates,
                r.get(Baseclass_.id),
                r.get(WhatsAppHealthNotificationTemplate_.whatsAppConfiguration).get(Baseclass_.id),
                r.get(WhatsAppHealthNotificationTemplate_.eventType),
                r.get(WhatsAppHealthNotificationTemplate_.deliveryMode),
                r.get(WhatsAppHealthNotificationTemplate_.locale),
                r.get(WhatsAppHealthNotificationTemplate_.enabled));
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    private void addTemplatePredicates(HealthNotificationProviderTemplateFilter filter,
                                       CriteriaBuilder cb,
                                       List<Predicate> predicates,
                                       jakarta.persistence.criteria.Path<String> templateId,
                                       jakarta.persistence.criteria.Path<String> configurationId,
                                       jakarta.persistence.criteria.Path<HealthNotificationEventType> eventType,
                                       jakarta.persistence.criteria.Path<HealthNotificationDeliveryMode> deliveryMode,
                                       jakarta.persistence.criteria.Path<String> locale,
                                       jakarta.persistence.criteria.Path<Boolean> enabled) {
        if (filter.getTemplateIds() != null && !filter.getTemplateIds().isEmpty()) {
            predicates.add(templateId.in(filter.getTemplateIds()));
        }
        if (filter.getConfigurationIds() != null && !filter.getConfigurationIds().isEmpty()) {
            predicates.add(configurationId.in(filter.getConfigurationIds()));
        }
        if (filter.getEventTypes() != null && !filter.getEventTypes().isEmpty()) {
            predicates.add(eventType.in(filter.getEventTypes()));
        }
        if (filter.getDeliveryModes() != null && !filter.getDeliveryModes().isEmpty()) {
            predicates.add(deliveryMode.in(filter.getDeliveryModes()));
        }
        if (filter.getLocales() != null && !filter.getLocales().isEmpty()) {
            predicates.add(locale.in(filter.getLocales()));
        }
        if (filter.getEnabled() != null) predicates.add(cb.equal(enabled, filter.getEnabled()));
    }

    public SendGridHealthNotificationConfiguration findEnabledSendGridConfiguration(String tenantId) {
        return findEnabledConfiguration(SendGridHealthNotificationConfiguration.class, tenantId,
                SendGridHealthNotificationConfiguration_.enabled);
    }

    public WhatsAppCloudHealthNotificationConfiguration findEnabledWhatsAppConfiguration(String tenantId) {
        return findEnabledConfiguration(WhatsAppCloudHealthNotificationConfiguration.class, tenantId,
                WhatsAppCloudHealthNotificationConfiguration_.enabled);
    }

    private <T extends Baseclass> T findEnabledConfiguration(
            Class<T> type,
            String tenantId,
            jakarta.persistence.metamodel.SingularAttribute<? super T, Boolean> enabledAttribute) {
        if (tenantId == null) return null;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(type);
        Root<T> r = q.from(type);
        q.select(r).where(
                cb.equal(r.get(Baseclass_.tenant).get(SecurityTenant_.id), tenantId),
                cb.isTrue(r.get(enabledAttribute)),
                cb.isFalse(r.get(Baseclass_.softDelete)))
                .orderBy(cb.desc(r.get(Baseclass_.updateDate)), cb.desc(r.get(Baseclass_.creationDate)));
        List<T> result = em.createQuery(q).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<SendGridHealthNotificationConfiguration> findOtherEnabledSendGridConfigurations(
            String tenantId, String excludedId) {
        return findOtherEnabledConfigurations(SendGridHealthNotificationConfiguration.class, tenantId, excludedId,
                SendGridHealthNotificationConfiguration_.enabled);
    }

    public List<WhatsAppCloudHealthNotificationConfiguration> findOtherEnabledWhatsAppConfigurations(
            String tenantId, String excludedId) {
        return findOtherEnabledConfigurations(WhatsAppCloudHealthNotificationConfiguration.class, tenantId, excludedId,
                WhatsAppCloudHealthNotificationConfiguration_.enabled);
    }

    private <T extends Baseclass> List<T> findOtherEnabledConfigurations(
            Class<T> type,
            String tenantId,
            String excludedId,
            jakarta.persistence.metamodel.SingularAttribute<? super T, Boolean> enabledAttribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(type);
        Root<T> r = q.from(type);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(r.get(Baseclass_.tenant).get(SecurityTenant_.id), tenantId));
        predicates.add(cb.isTrue(r.get(enabledAttribute)));
        predicates.add(cb.isFalse(r.get(Baseclass_.softDelete)));
        if (excludedId != null) predicates.add(cb.notEqual(r.get(Baseclass_.id), excludedId));
        q.select(r).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getResultList();
    }

    public SendGridHealthNotificationTemplate findBestSendGridTemplate(
            String configurationId,
            HealthNotificationEventType eventType,
            HealthNotificationDeliveryMode deliveryMode,
            String locale) {
        List<SendGridHealthNotificationTemplate> candidates = listRuntimeSendGridTemplates(configurationId, eventType, deliveryMode);
        return candidates.stream()
                .map(template -> new ScoredTemplate<>(template, score(
                        template.getEventType(), template.getDeliveryMode(), template.getLocale(),
                        eventType, deliveryMode, locale, template.getPriority())))
                .filter(scored -> scored.score() != Integer.MIN_VALUE)
                .max(Comparator.comparingInt((ScoredTemplate<SendGridHealthNotificationTemplate> scored) -> scored.score()))
                .map(ScoredTemplate::template)
                .orElse(null);
    }

    public WhatsAppHealthNotificationTemplate findBestWhatsAppTemplate(
            String configurationId,
            HealthNotificationEventType eventType,
            HealthNotificationDeliveryMode deliveryMode,
            String locale) {
        List<WhatsAppHealthNotificationTemplate> candidates = listRuntimeWhatsAppTemplates(configurationId, eventType, deliveryMode);
        WhatsAppHealthNotificationTemplate selected = candidates.stream()
                .map(template -> new ScoredTemplate<>(template, score(
                        template.getEventType(), template.getDeliveryMode(), template.getLocale(),
                        eventType, deliveryMode, locale, template.getPriority())))
                .filter(scored -> scored.score() != Integer.MIN_VALUE)
                .max(Comparator.comparingInt((ScoredTemplate<WhatsAppHealthNotificationTemplate> scored) -> scored.score()))
                .map(ScoredTemplate::template)
                .orElse(null);
        if (selected != null) populateWhatsAppParameters(List.of(selected));
        return selected;
    }

    private int score(HealthNotificationEventType configuredEvent,
                      HealthNotificationDeliveryMode configuredMode,
                      String configuredLocale,
                      HealthNotificationEventType requestedEvent,
                      HealthNotificationDeliveryMode requestedMode,
                      String requestedLocale,
                      int priority) {
        if (configuredEvent != null && configuredEvent != requestedEvent) return Integer.MIN_VALUE;
        if (configuredMode != null && configuredMode != requestedMode) return Integer.MIN_VALUE;
        String configured = normalizeLocale(configuredLocale);
        String requested = normalizeLocale(requestedLocale);
        int localeScore;
        if (configured.equals(requested)) localeScore = 20;
        else if (configured.equals(language(requested))) localeScore = 15;
        else if (configured.equals("*") || configured.equals("en")) localeScore = 5;
        else return Integer.MIN_VALUE;
        int eventScore = configuredEvent == requestedEvent ? 40 : 10;
        int modeScore = configuredMode == requestedMode ? 30 : 10;
        return priority * 100 + eventScore + modeScore + localeScore;
    }

    private String normalizeLocale(String locale) {
        return locale == null || locale.isBlank() ? "en" : locale.trim().replace('-', '_').toLowerCase(Locale.ROOT);
    }

    private String language(String locale) {
        int separator = locale.indexOf('_');
        return separator < 0 ? locale : locale.substring(0, separator);
    }

    private List<SendGridHealthNotificationTemplate> listRuntimeSendGridTemplates(
            String configurationId,
            HealthNotificationEventType eventType,
            HealthNotificationDeliveryMode deliveryMode) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SendGridHealthNotificationTemplate> q = cb.createQuery(SendGridHealthNotificationTemplate.class);
        Root<SendGridHealthNotificationTemplate> r = q.from(SendGridHealthNotificationTemplate.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(r.get(SendGridHealthNotificationTemplate_.sendGridConfiguration).get(Baseclass_.id), configurationId));
        predicates.add(eventType == null
                ? cb.isNull(r.get(SendGridHealthNotificationTemplate_.eventType))
                : cb.or(cb.isNull(r.get(SendGridHealthNotificationTemplate_.eventType)),
                cb.equal(r.get(SendGridHealthNotificationTemplate_.eventType), eventType)));
        predicates.add(deliveryMode == null
                ? cb.isNull(r.get(SendGridHealthNotificationTemplate_.deliveryMode))
                : cb.or(cb.isNull(r.get(SendGridHealthNotificationTemplate_.deliveryMode)),
                cb.equal(r.get(SendGridHealthNotificationTemplate_.deliveryMode), deliveryMode)));
        predicates.add(cb.isTrue(r.get(SendGridHealthNotificationTemplate_.enabled)));
        predicates.add(cb.isFalse(r.get(Baseclass_.softDelete)));
        q.select(r).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getResultList();
    }

    private List<WhatsAppHealthNotificationTemplate> listRuntimeWhatsAppTemplates(
            String configurationId,
            HealthNotificationEventType eventType,
            HealthNotificationDeliveryMode deliveryMode) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WhatsAppHealthNotificationTemplate> q = cb.createQuery(WhatsAppHealthNotificationTemplate.class);
        Root<WhatsAppHealthNotificationTemplate> r = q.from(WhatsAppHealthNotificationTemplate.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(r.get(WhatsAppHealthNotificationTemplate_.whatsAppConfiguration).get(Baseclass_.id), configurationId));
        predicates.add(eventType == null
                ? cb.isNull(r.get(WhatsAppHealthNotificationTemplate_.eventType))
                : cb.or(cb.isNull(r.get(WhatsAppHealthNotificationTemplate_.eventType)),
                cb.equal(r.get(WhatsAppHealthNotificationTemplate_.eventType), eventType)));
        predicates.add(deliveryMode == null
                ? cb.isNull(r.get(WhatsAppHealthNotificationTemplate_.deliveryMode))
                : cb.or(cb.isNull(r.get(WhatsAppHealthNotificationTemplate_.deliveryMode)),
                cb.equal(r.get(WhatsAppHealthNotificationTemplate_.deliveryMode), deliveryMode)));
        predicates.add(cb.isTrue(r.get(WhatsAppHealthNotificationTemplate_.enabled)));
        predicates.add(cb.isFalse(r.get(Baseclass_.softDelete)));
        q.select(r).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getResultList();
    }

    public void populateWhatsAppParameters(List<WhatsAppHealthNotificationTemplate> templates) {
        if (templates == null || templates.isEmpty()) return;
        List<String> ids = templates.stream().map(Baseclass::getId).toList();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WhatsAppHealthNotificationTemplateParameter> q = cb.createQuery(WhatsAppHealthNotificationTemplateParameter.class);
        Root<WhatsAppHealthNotificationTemplateParameter> r = q.from(WhatsAppHealthNotificationTemplateParameter.class);
        q.select(r).where(
                r.get(WhatsAppHealthNotificationTemplateParameter_.whatsAppTemplate).get(Baseclass_.id).in(ids),
                cb.isFalse(r.get(Baseclass_.softDelete)))
                .orderBy(
                        cb.asc(r.get(WhatsAppHealthNotificationTemplateParameter_.componentType)),
                        cb.asc(r.get(WhatsAppHealthNotificationTemplateParameter_.buttonIndex)),
                        cb.asc(r.get(WhatsAppHealthNotificationTemplateParameter_.parameterOrder)));
        List<WhatsAppHealthNotificationTemplateParameter> parameters = em.createQuery(q).getResultList();
        for (WhatsAppHealthNotificationTemplate template : templates) {
            template.setParameters(parameters.stream()
                    .filter(parameter -> parameter.getWhatsAppTemplate().getId().equals(template.getId()))
                    .toList());
        }
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> type, SecurityContext securityContext) {
        return securedBasicRepository.getByIdOrNull(id, type, securityContext);
    }

    private record ScoredTemplate<T>(T template, int score) { }

    @Transactional
    public void massMerge(Collection<?> entities) {
        if (entities != null && !entities.isEmpty()) securedBasicRepository.massMerge(new ArrayList<>(entities));
    }
}
