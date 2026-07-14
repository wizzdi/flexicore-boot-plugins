package com.wizzdi.basic.iot.service.service;

import com.flexicore.model.Baseclass;
import com.wizzdi.basic.iot.model.HealthNotificationTemplateValueSource;
import com.wizzdi.basic.iot.model.SendGridHealthNotificationConfiguration;
import com.wizzdi.basic.iot.model.SendGridHealthNotificationTemplate;
import com.wizzdi.basic.iot.model.WhatsAppCloudHealthNotificationConfiguration;
import com.wizzdi.basic.iot.model.WhatsAppHealthNotificationTemplate;
import com.wizzdi.basic.iot.model.WhatsAppHealthNotificationTemplateParameter;
import com.wizzdi.basic.iot.model.WhatsAppTemplateComponentType;
import com.wizzdi.basic.iot.service.data.HealthNotificationProviderRepository;
import com.wizzdi.basic.iot.service.notification.HealthNotificationItem;
import com.wizzdi.basic.iot.service.notification.HealthNotificationMessage;
import com.wizzdi.basic.iot.service.notification.SendGridHealthNotificationRuntimeConfiguration;
import com.wizzdi.basic.iot.service.notification.WhatsAppCloudHealthNotificationRuntimeConfiguration;
import com.wizzdi.basic.iot.service.notification.WhatsAppHealthNotificationRuntimeParameter;
import com.wizzdi.basic.iot.service.request.HealthNotificationProviderConfigurationFilter;
import com.wizzdi.basic.iot.service.request.HealthNotificationProviderTemplateFilter;
import com.wizzdi.basic.iot.service.request.SendGridHealthNotificationConfigurationCreate;
import com.wizzdi.basic.iot.service.request.SendGridHealthNotificationConfigurationUpdate;
import com.wizzdi.basic.iot.service.request.SendGridHealthNotificationTemplateCreate;
import com.wizzdi.basic.iot.service.request.SendGridHealthNotificationTemplateUpdate;
import com.wizzdi.basic.iot.service.request.WhatsAppCloudHealthNotificationConfigurationCreate;
import com.wizzdi.basic.iot.service.request.WhatsAppCloudHealthNotificationConfigurationUpdate;
import com.wizzdi.basic.iot.service.request.WhatsAppHealthNotificationTemplateCreate;
import com.wizzdi.basic.iot.service.request.WhatsAppHealthNotificationTemplateParameterCreate;
import com.wizzdi.basic.iot.service.request.WhatsAppHealthNotificationTemplateUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.encryption.service.CommonEncryptionService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Extension
@Component
public class HealthNotificationProviderConfigurationService implements Plugin {
    private static final String SENDGRID_PROVIDER = "SENDGRID";
    private static final String WHATSAPP_PROVIDER = "WHATSAPP_CLOUD";

    @Autowired
    private HealthNotificationProviderRepository repository;
    @Autowired
    private BasicService basicService;
    @Autowired
    private DerivedEntitySecurityService derivedEntitySecurityService;
    @Autowired
    private CommonEncryptionService encryptionService;
    @Value("${basic.iot.health.notifications.providers.allowCustomEndpoints:false}")
    private boolean allowCustomEndpoints;
    @Value("${basic.iot.health.notifications.providers.allowInsecureEndpoints:false}")
    private boolean allowInsecureEndpoints;

    public void validateFiltering(HealthNotificationProviderConfigurationFilter filter,
                                  SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    public void validateFiltering(HealthNotificationProviderTemplateFilter filter,
                                  SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    public void validate(SendGridHealthNotificationConfigurationCreate create,
                         SecurityContext securityContext) {
        basicService.validate(create, securityContext);
        SendGridHealthNotificationConfiguration existing = null;
        if (create instanceof SendGridHealthNotificationConfigurationUpdate update) {
            existing = repository.getByIdOrNull(update.getId(), SendGridHealthNotificationConfiguration.class, securityContext);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No accessible SendGrid configuration with id " + update.getId());
            }
            update.setConfiguration(existing);
        }
        String baseUrl = value(create.getApiBaseUrl(), existing == null ? "https://api.sendgrid.com" : existing.getApiBaseUrl());
        validateProviderUrl(baseUrl, Set.of("https://api.sendgrid.com", "https://api.eu.sendgrid.com"));
        String fromEmail = value(create.getFromEmail(), existing == null ? null : existing.getFromEmail());
        if (fromEmail == null || !fromEmail.contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid fromEmail is required");
        }
        String replyToEmail = value(create.getReplyToEmail(), existing == null ? null : existing.getReplyToEmail());
        if (replyToEmail != null && !replyToEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid replyToEmail");
        }
        String defaultTemplateId = value(create.getDefaultTemplateId(),
                existing == null ? null : existing.getDefaultTemplateId());
        if (defaultTemplateId != null && !defaultTemplateId.matches("d-[A-Za-z0-9]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "defaultTemplateId must use the form d-<id>");
        }
        boolean hasSecret = create.getApiKey() != null && !create.getApiKey().isBlank();
        boolean clearSecret = Boolean.TRUE.equals(create.getClearApiKey());
        if (hasSecret && clearSecret) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "apiKey and clearApiKey cannot be supplied together");
        }
        if (existing == null && !hasSecret) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "apiKey is required for a new SendGrid configuration");
        }
    }

    public void validate(WhatsAppCloudHealthNotificationConfigurationCreate create,
                         SecurityContext securityContext) {
        basicService.validate(create, securityContext);
        WhatsAppCloudHealthNotificationConfiguration existing = null;
        if (create instanceof WhatsAppCloudHealthNotificationConfigurationUpdate update) {
            existing = repository.getByIdOrNull(update.getId(), WhatsAppCloudHealthNotificationConfiguration.class, securityContext);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No accessible WhatsApp Cloud configuration with id " + update.getId());
            }
            update.setConfiguration(existing);
        }
        String baseUrl = value(create.getApiBaseUrl(), existing == null ? "https://graph.facebook.com" : existing.getApiBaseUrl());
        validateProviderUrl(baseUrl, Set.of("https://graph.facebook.com"));
        String graphVersion = value(create.getGraphApiVersion(), existing == null ? "v25.0" : existing.getGraphApiVersion());
        if (!graphVersion.matches("v[0-9]+\\.[0-9]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "graphApiVersion must use the form v25.0");
        }
        String phoneNumberId = value(create.getPhoneNumberId(), existing == null ? null : existing.getPhoneNumberId());
        if (phoneNumberId == null || !phoneNumberId.matches("[0-9]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A numeric phoneNumberId is required");
        }
        String businessAccountId = value(create.getWhatsAppBusinessAccountId(),
                existing == null ? null : existing.getWhatsAppBusinessAccountId());
        if (businessAccountId != null && !businessAccountId.matches("[0-9]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "whatsAppBusinessAccountId must be numeric");
        }
        String defaultTemplateName = value(create.getDefaultTemplateName(),
                existing == null ? null : existing.getDefaultTemplateName());
        if (defaultTemplateName != null && !defaultTemplateName.matches("[a-z0-9_]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "defaultTemplateName may contain only lowercase letters, digits and underscores");
        }
        String defaultLanguageCode = value(create.getDefaultLanguageCode(),
                existing == null ? "en_US" : existing.getDefaultLanguageCode());
        if (defaultLanguageCode != null && !defaultLanguageCode.matches("[a-z]{2,3}(_[A-Z]{2})?")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "defaultLanguageCode must use a form such as en_US");
        }
        boolean hasSecret = create.getAccessToken() != null && !create.getAccessToken().isBlank();
        boolean clearSecret = Boolean.TRUE.equals(create.getClearAccessToken());
        if (hasSecret && clearSecret) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "accessToken and clearAccessToken cannot be supplied together");
        }
        if (existing == null && !hasSecret) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "accessToken is required for a new WhatsApp Cloud configuration");
        }
    }

    public void validate(SendGridHealthNotificationTemplateCreate create,
                         SecurityContext securityContext) {
        basicService.validate(create, securityContext);
        SendGridHealthNotificationTemplate existing = null;
        if (create instanceof SendGridHealthNotificationTemplateUpdate update) {
            existing = repository.getByIdOrNull(update.getId(), SendGridHealthNotificationTemplate.class, securityContext);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No accessible SendGrid template with id " + update.getId());
            }
            update.setTemplate(existing);
        }
        String configurationId = value(create.getConfigurationId(),
                existing == null || existing.getSendGridConfiguration() == null
                        ? null : existing.getSendGridConfiguration().getId());
        if (repository.getByIdOrNull(configurationId, SendGridHealthNotificationConfiguration.class, securityContext) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid accessible configurationId is required");
        }
        String templateId = value(create.getTemplateId(), existing == null ? null : existing.getTemplateId());
        if (templateId == null || templateId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "templateId is required");
        }
        if (!templateId.matches("d-[A-Za-z0-9]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "SendGrid dynamic templateId must use the form d-<id>");
        }
        validateLocale(create.getLocale());
    }

    public void validate(WhatsAppHealthNotificationTemplateCreate create,
                         SecurityContext securityContext) {
        basicService.validate(create, securityContext);
        WhatsAppHealthNotificationTemplate existing = null;
        if (create instanceof WhatsAppHealthNotificationTemplateUpdate update) {
            existing = repository.getByIdOrNull(update.getId(), WhatsAppHealthNotificationTemplate.class, securityContext);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No accessible WhatsApp template with id " + update.getId());
            }
            update.setTemplate(existing);
        }
        String configurationId = value(create.getConfigurationId(),
                existing == null || existing.getWhatsAppConfiguration() == null
                        ? null : existing.getWhatsAppConfiguration().getId());
        if (repository.getByIdOrNull(configurationId, WhatsAppCloudHealthNotificationConfiguration.class, securityContext) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid accessible configurationId is required");
        }
        String templateName = value(create.getTemplateName(), existing == null ? null : existing.getTemplateName());
        if (templateName == null || templateName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "templateName is required");
        }
        if (!templateName.matches("[a-z0-9_]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "WhatsApp templateName may contain only lowercase letters, digits and underscores");
        }
        String languageCode = value(create.getLanguageCode(), existing == null ? "en_US" : existing.getLanguageCode());
        if (languageCode == null || !languageCode.matches("[a-z]{2,3}(_[A-Z]{2})?")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "WhatsApp languageCode must use a form such as en_US");
        }
        validateLocale(create.getLocale());
        validateWhatsAppParameters(create.getParameters(), securityContext);
    }

    private void validateWhatsAppParameters(List<WhatsAppHealthNotificationTemplateParameterCreate> parameters,
                                            SecurityContext securityContext) {
        if (parameters == null) return;
        Set<String> positions = new HashSet<>();
        for (WhatsAppHealthNotificationTemplateParameterCreate parameter : parameters) {
            basicService.validate(parameter, securityContext);
            WhatsAppTemplateComponentType component = parameter.getComponentType() == null
                    ? WhatsAppTemplateComponentType.BODY : parameter.getComponentType();
            int buttonIndex = parameter.getButtonIndex() == null ? 0 : parameter.getButtonIndex();
            int order = parameter.getParameterOrder() == null ? 0 : parameter.getParameterOrder();
            if (buttonIndex < 0 || order < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "buttonIndex and parameterOrder must be non-negative");
            }
            String position = component + ":" + buttonIndex + ":" + order;
            if (!positions.add(position)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Duplicate WhatsApp template parameter position " + position);
            }
            HealthNotificationTemplateValueSource source = parameter.getValueSource() == null
                    ? HealthNotificationTemplateValueSource.LITERAL : parameter.getValueSource();
            if (source == HealthNotificationTemplateValueSource.LITERAL
                    && (parameter.getLiteralValue() == null || parameter.getLiteralValue().isBlank())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "literalValue is required for LITERAL parameters");
            }
            if (component == WhatsAppTemplateComponentType.BUTTON && parameter.getButtonSubType() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "buttonSubType is required for BUTTON parameters");
            }
            if (parameter.getTimeZone() != null) {
                try {
                    ZoneId.of(parameter.getTimeZone());
                } catch (DateTimeException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid parameter timeZone " + parameter.getTimeZone());
                }
            }
            if (parameter.getDateTimePattern() != null && !parameter.getDateTimePattern().isBlank()) {
                try {
                    DateTimeFormatter.ofPattern(parameter.getDateTimePattern());
                } catch (IllegalArgumentException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid dateTimePattern " + parameter.getDateTimePattern());
                }
            }
        }
    }

    public PaginationResponse<SendGridHealthNotificationConfiguration> getAllSendGridConfigurations(
            SecurityContext securityContext,
            HealthNotificationProviderConfigurationFilter filter) {
        List<SendGridHealthNotificationConfiguration> list = repository.listSendGridConfigurations(securityContext, filter);
        return new PaginationResponse<>(list, filter, repository.countSendGridConfigurations(securityContext, filter));
    }

    public PaginationResponse<WhatsAppCloudHealthNotificationConfiguration> getAllWhatsAppConfigurations(
            SecurityContext securityContext,
            HealthNotificationProviderConfigurationFilter filter) {
        List<WhatsAppCloudHealthNotificationConfiguration> list = repository.listWhatsAppConfigurations(securityContext, filter);
        return new PaginationResponse<>(list, filter, repository.countWhatsAppConfigurations(securityContext, filter));
    }

    public PaginationResponse<SendGridHealthNotificationTemplate> getAllSendGridTemplates(
            SecurityContext securityContext,
            HealthNotificationProviderTemplateFilter filter) {
        List<SendGridHealthNotificationTemplate> list = repository.listSendGridTemplates(securityContext, filter);
        return new PaginationResponse<>(list, filter, repository.countSendGridTemplates(securityContext, filter));
    }

    public PaginationResponse<WhatsAppHealthNotificationTemplate> getAllWhatsAppTemplates(
            SecurityContext securityContext,
            HealthNotificationProviderTemplateFilter filter) {
        List<WhatsAppHealthNotificationTemplate> list = repository.listWhatsAppTemplates(securityContext, filter);
        return new PaginationResponse<>(list, filter, repository.countWhatsAppTemplates(securityContext, filter));
    }

    @Transactional
    public SendGridHealthNotificationConfiguration createSendGrid(
            SendGridHealthNotificationConfigurationCreate create,
            SecurityContext securityContext) {
        SendGridHealthNotificationConfiguration configuration = new SendGridHealthNotificationConfiguration();
        configuration.setId(UUID.randomUUID().toString());
        updateSendGridNoMerge(configuration, create);
        BaseclassService.createSecurityObjectNoMerge(configuration, securityContext);
        updateSendGridSecret(configuration, create);
        List<Object> toMerge = new ArrayList<>();
        toMerge.add(configuration);
        disableOtherSendGrid(configuration, toMerge);
        repository.massMerge(toMerge);
        return configuration;
    }

    @Transactional
    public SendGridHealthNotificationConfiguration updateSendGrid(
            SendGridHealthNotificationConfigurationUpdate update,
            SecurityContext securityContext) {
        SendGridHealthNotificationConfiguration configuration = update.getConfiguration();
        updateSendGridNoMerge(configuration, update);
        updateSendGridSecret(configuration, update);
        List<Object> toMerge = new ArrayList<>();
        toMerge.add(configuration);
        disableOtherSendGrid(configuration, toMerge);
        repository.massMerge(toMerge);
        return configuration;
    }

    private void updateSendGridNoMerge(SendGridHealthNotificationConfiguration configuration,
                                       SendGridHealthNotificationConfigurationCreate create) {
        basicService.updateBasicNoMerge(create, configuration);
        if (create.getEnabled() != null) configuration.setEnabled(create.getEnabled());
        if (create.getApiBaseUrl() != null) configuration.setApiBaseUrl(normalizeBaseUrl(create.getApiBaseUrl()));
        if (create.getFromEmail() != null) configuration.setFromEmail(trimToNull(create.getFromEmail()));
        if (create.getFromName() != null) configuration.setFromName(trimToNull(create.getFromName()));
        if (create.getReplyToEmail() != null) configuration.setReplyToEmail(trimToNull(create.getReplyToEmail()));
        if (create.getReplyToName() != null) configuration.setReplyToName(trimToNull(create.getReplyToName()));
        if (create.getOnBehalfOf() != null) configuration.setOnBehalfOf(trimToNull(create.getOnBehalfOf()));
        if (create.getDefaultTemplateId() != null) configuration.setDefaultTemplateId(trimToNull(create.getDefaultTemplateId()));
    }

    private void updateSendGridSecret(SendGridHealthNotificationConfiguration configuration,
                                      SendGridHealthNotificationConfigurationCreate create) {
        if (Boolean.TRUE.equals(create.getClearApiKey())) {
            configuration.setEnabled(false);
            configuration.setEncryptedApiKey(null);
            configuration.setApiKeyFingerprint(null);
            configuration.setCredentialUpdatedAt(OffsetDateTime.now());
        } else if (create.getApiKey() != null && !create.getApiKey().isBlank()) {
            configuration.setEncryptedApiKey(encrypt(SENDGRID_PROVIDER, configuration, create.getApiKey().trim()));
            configuration.setApiKeyFingerprint(fingerprint(create.getApiKey().trim()));
            configuration.setCredentialUpdatedAt(OffsetDateTime.now());
        }
    }

    private void disableOtherSendGrid(SendGridHealthNotificationConfiguration configuration,
                                      List<Object> toMerge) {
        if (!configuration.isEnabled() || configuration.getTenant() == null) return;
        for (SendGridHealthNotificationConfiguration other : repository.findOtherEnabledSendGridConfigurations(
                configuration.getTenant().getId(), configuration.getId())) {
            other.setEnabled(false);
            toMerge.add(other);
        }
    }

    @Transactional
    public WhatsAppCloudHealthNotificationConfiguration createWhatsApp(
            WhatsAppCloudHealthNotificationConfigurationCreate create,
            SecurityContext securityContext) {
        WhatsAppCloudHealthNotificationConfiguration configuration = new WhatsAppCloudHealthNotificationConfiguration();
        configuration.setId(UUID.randomUUID().toString());
        updateWhatsAppNoMerge(configuration, create);
        BaseclassService.createSecurityObjectNoMerge(configuration, securityContext);
        updateWhatsAppSecret(configuration, create);
        List<Object> toMerge = new ArrayList<>();
        toMerge.add(configuration);
        disableOtherWhatsApp(configuration, toMerge);
        repository.massMerge(toMerge);
        return configuration;
    }

    @Transactional
    public WhatsAppCloudHealthNotificationConfiguration updateWhatsApp(
            WhatsAppCloudHealthNotificationConfigurationUpdate update,
            SecurityContext securityContext) {
        WhatsAppCloudHealthNotificationConfiguration configuration = update.getConfiguration();
        updateWhatsAppNoMerge(configuration, update);
        updateWhatsAppSecret(configuration, update);
        List<Object> toMerge = new ArrayList<>();
        toMerge.add(configuration);
        disableOtherWhatsApp(configuration, toMerge);
        repository.massMerge(toMerge);
        return configuration;
    }

    private void updateWhatsAppNoMerge(WhatsAppCloudHealthNotificationConfiguration configuration,
                                       WhatsAppCloudHealthNotificationConfigurationCreate create) {
        basicService.updateBasicNoMerge(create, configuration);
        if (create.getEnabled() != null) configuration.setEnabled(create.getEnabled());
        if (create.getApiBaseUrl() != null) configuration.setApiBaseUrl(normalizeBaseUrl(create.getApiBaseUrl()));
        if (create.getGraphApiVersion() != null) configuration.setGraphApiVersion(create.getGraphApiVersion().trim());
        if (create.getPhoneNumberId() != null) configuration.setPhoneNumberId(trimToNull(create.getPhoneNumberId()));
        if (create.getWhatsAppBusinessAccountId() != null) configuration.setWhatsAppBusinessAccountId(trimToNull(create.getWhatsAppBusinessAccountId()));
        if (create.getDefaultTemplateName() != null) configuration.setDefaultTemplateName(trimToNull(create.getDefaultTemplateName()));
        if (create.getDefaultLanguageCode() != null) configuration.setDefaultLanguageCode(trimToNull(create.getDefaultLanguageCode()));
    }

    private void updateWhatsAppSecret(WhatsAppCloudHealthNotificationConfiguration configuration,
                                      WhatsAppCloudHealthNotificationConfigurationCreate create) {
        if (Boolean.TRUE.equals(create.getClearAccessToken())) {
            configuration.setEnabled(false);
            configuration.setEncryptedAccessToken(null);
            configuration.setAccessTokenFingerprint(null);
            configuration.setCredentialUpdatedAt(OffsetDateTime.now());
        } else if (create.getAccessToken() != null && !create.getAccessToken().isBlank()) {
            configuration.setEncryptedAccessToken(encrypt(WHATSAPP_PROVIDER, configuration, create.getAccessToken().trim()));
            configuration.setAccessTokenFingerprint(fingerprint(create.getAccessToken().trim()));
            configuration.setCredentialUpdatedAt(OffsetDateTime.now());
        }
    }

    private void disableOtherWhatsApp(WhatsAppCloudHealthNotificationConfiguration configuration,
                                      List<Object> toMerge) {
        if (!configuration.isEnabled() || configuration.getTenant() == null) return;
        for (WhatsAppCloudHealthNotificationConfiguration other : repository.findOtherEnabledWhatsAppConfigurations(
                configuration.getTenant().getId(), configuration.getId())) {
            other.setEnabled(false);
            toMerge.add(other);
        }
    }

    @Transactional
    public SendGridHealthNotificationTemplate createSendGridTemplate(
            SendGridHealthNotificationTemplateCreate create,
            SecurityContext securityContext) {
        SendGridHealthNotificationConfiguration configuration = repository.getByIdOrNull(
                create.getConfigurationId(), SendGridHealthNotificationConfiguration.class, securityContext);
        SendGridHealthNotificationTemplate template = new SendGridHealthNotificationTemplate();
        template.setId(UUID.randomUUID().toString());
        updateSendGridTemplateNoMerge(template, configuration, create);
        BaseclassService.createSecurityObjectNoMerge(template,
                derivedEntitySecurityService.creationContext(securityContext, configuration));
        derivedEntitySecurityService.setTenantFrom(template, configuration);
        repository.massMerge(List.of(template));
        return template;
    }

    @Transactional
    public SendGridHealthNotificationTemplate updateSendGridTemplate(
            SendGridHealthNotificationTemplateUpdate update,
            SecurityContext securityContext) {
        SendGridHealthNotificationTemplate template = update.getTemplate();
        SendGridHealthNotificationConfiguration configuration = update.getConfigurationId() == null
                ? template.getSendGridConfiguration()
                : repository.getByIdOrNull(update.getConfigurationId(), SendGridHealthNotificationConfiguration.class, securityContext);
        updateSendGridTemplateNoMerge(template, configuration, update);
        derivedEntitySecurityService.setTenantFrom(template, configuration);
        repository.massMerge(List.of(template));
        return template;
    }

    private void updateSendGridTemplateNoMerge(SendGridHealthNotificationTemplate template,
                                               SendGridHealthNotificationConfiguration configuration,
                                               SendGridHealthNotificationTemplateCreate create) {
        basicService.updateBasicNoMerge(create, template);
        template.setSendGridConfiguration(configuration);
        if (create.getEventType() != null || template.getEventType() == null) template.setEventType(create.getEventType());
        if (create.getDeliveryMode() != null || template.getDeliveryMode() == null) template.setDeliveryMode(create.getDeliveryMode());
        if (create.getLocale() != null) template.setLocale(normalizeLocale(create.getLocale()));
        if (create.getTemplateId() != null) template.setTemplateId(create.getTemplateId().trim());
        if (create.getEnabled() != null) template.setEnabled(create.getEnabled());
        if (create.getPriority() != null) template.setPriority(create.getPriority());
    }

    @Transactional
    public WhatsAppHealthNotificationTemplate createWhatsAppTemplate(
            WhatsAppHealthNotificationTemplateCreate create,
            SecurityContext securityContext) {
        WhatsAppCloudHealthNotificationConfiguration configuration = repository.getByIdOrNull(
                create.getConfigurationId(), WhatsAppCloudHealthNotificationConfiguration.class, securityContext);
        WhatsAppHealthNotificationTemplate template = new WhatsAppHealthNotificationTemplate();
        template.setId(UUID.randomUUID().toString());
        updateWhatsAppTemplateNoMerge(template, configuration, create);
        BaseclassService.createSecurityObjectNoMerge(template,
                derivedEntitySecurityService.creationContext(securityContext, configuration));
        derivedEntitySecurityService.setTenantFrom(template, configuration);
        List<Object> toMerge = new ArrayList<>();
        toMerge.add(template);
        syncWhatsAppParameters(template, create.getParameters(), securityContext, toMerge);
        repository.massMerge(toMerge);
        repository.populateWhatsAppParameters(List.of(template));
        return template;
    }

    @Transactional
    public WhatsAppHealthNotificationTemplate updateWhatsAppTemplate(
            WhatsAppHealthNotificationTemplateUpdate update,
            SecurityContext securityContext) {
        WhatsAppHealthNotificationTemplate template = update.getTemplate();
        WhatsAppCloudHealthNotificationConfiguration configuration = update.getConfigurationId() == null
                ? template.getWhatsAppConfiguration()
                : repository.getByIdOrNull(update.getConfigurationId(), WhatsAppCloudHealthNotificationConfiguration.class, securityContext);
        updateWhatsAppTemplateNoMerge(template, configuration, update);
        derivedEntitySecurityService.setTenantFrom(template, configuration);
        List<Object> toMerge = new ArrayList<>();
        toMerge.add(template);
        if (update.getParameters() != null) {
            syncWhatsAppParameters(template, update.getParameters(), securityContext, toMerge);
        }
        repository.massMerge(toMerge);
        repository.populateWhatsAppParameters(List.of(template));
        return template;
    }

    private void updateWhatsAppTemplateNoMerge(WhatsAppHealthNotificationTemplate template,
                                               WhatsAppCloudHealthNotificationConfiguration configuration,
                                               WhatsAppHealthNotificationTemplateCreate create) {
        basicService.updateBasicNoMerge(create, template);
        template.setWhatsAppConfiguration(configuration);
        if (create.getEventType() != null || template.getEventType() == null) template.setEventType(create.getEventType());
        if (create.getDeliveryMode() != null || template.getDeliveryMode() == null) template.setDeliveryMode(create.getDeliveryMode());
        if (create.getLocale() != null) template.setLocale(normalizeLocale(create.getLocale()));
        if (create.getTemplateName() != null) template.setTemplateName(create.getTemplateName().trim());
        if (create.getLanguageCode() != null) template.setLanguageCode(create.getLanguageCode().trim());
        if (create.getEnabled() != null) template.setEnabled(create.getEnabled());
        if (create.getPriority() != null) template.setPriority(create.getPriority());
    }

    private void syncWhatsAppParameters(WhatsAppHealthNotificationTemplate template,
                                        List<WhatsAppHealthNotificationTemplateParameterCreate> requested,
                                        SecurityContext securityContext,
                                        List<Object> toMerge) {
        List<WhatsAppHealthNotificationTemplateParameter> existing = template.getParameters();
        if ((existing == null || existing.isEmpty()) && template.getId() != null) {
            repository.populateWhatsAppParameters(List.of(template));
            existing = template.getParameters();
        }
        Map<String, WhatsAppHealthNotificationTemplateParameter> byId = new HashMap<>();
        if (existing != null) {
            for (WhatsAppHealthNotificationTemplateParameter parameter : existing) byId.put(parameter.getId(), parameter);
        }
        Set<String> retained = new HashSet<>();
        if (requested != null) {
            for (WhatsAppHealthNotificationTemplateParameterCreate create : requested) {
                WhatsAppHealthNotificationTemplateParameter parameter = create.getId() == null
                        ? new WhatsAppHealthNotificationTemplateParameter().setId(UUID.randomUUID().toString())
                        : byId.get(create.getId());
                if (parameter == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "No WhatsApp template parameter with id " + create.getId());
                }
                boolean newParameter = parameter.getWhatsAppTemplate() == null;
                basicService.updateBasicNoMerge(create, parameter);
                parameter.setWhatsAppTemplate(template);
                parameter.setComponentType(create.getComponentType() == null
                        ? WhatsAppTemplateComponentType.BODY : create.getComponentType());
                parameter.setButtonSubType(create.getButtonSubType());
                parameter.setButtonIndex(create.getButtonIndex() == null ? 0 : create.getButtonIndex());
                parameter.setParameterOrder(create.getParameterOrder() == null ? 0 : create.getParameterOrder());
                parameter.setValueSource(create.getValueSource() == null
                        ? HealthNotificationTemplateValueSource.LITERAL : create.getValueSource());
                parameter.setLiteralValue(create.getLiteralValue());
                parameter.setDateTimePattern(create.getDateTimePattern());
                parameter.setTimeZone(create.getTimeZone() == null ? "UTC" : create.getTimeZone());
                parameter.setFallbackValue(create.getFallbackValue());
                if (newParameter) {
                    BaseclassService.createSecurityObjectNoMerge(parameter,
                            derivedEntitySecurityService.creationContext(securityContext, template));
                }
                derivedEntitySecurityService.setTenantFrom(parameter, template);
                retained.add(parameter.getId());
                toMerge.add(parameter);
            }
        }
        if (existing != null) {
            for (WhatsAppHealthNotificationTemplateParameter parameter : existing) {
                if (!retained.contains(parameter.getId())) {
                    parameter.setSoftDelete(true);
                    toMerge.add(parameter);
                }
            }
        }
    }

    public boolean canResolveSendGrid(HealthNotificationMessage message) {
        try {
            return resolveSendGrid(message) != null;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public SendGridHealthNotificationRuntimeConfiguration resolveSendGrid(HealthNotificationMessage message) {
        if (message == null || message.tenantId() == null) return null;
        SendGridHealthNotificationConfiguration configuration = repository.findEnabledSendGridConfiguration(message.tenantId());
        if (configuration == null || !configuration.isApiKeyConfigured() || configuration.getFromEmail() == null) return null;
        SendGridHealthNotificationTemplate template = repository.findBestSendGridTemplate(
                configuration.getId(), firstEventType(message), message.deliveryMode(), message.locale());
        String templateId = template == null ? configuration.getDefaultTemplateId() : template.getTemplateId();
        return new SendGridHealthNotificationRuntimeConfiguration(
                configuration.getId(),
                configuration.getApiBaseUrl(),
                decrypt(SENDGRID_PROVIDER, configuration, configuration.getEncryptedApiKey()),
                configuration.getFromEmail(),
                configuration.getFromName(),
                configuration.getReplyToEmail(),
                configuration.getReplyToName(),
                configuration.getOnBehalfOf(),
                templateId);
    }

    public boolean canResolveWhatsApp(HealthNotificationMessage message) {
        try {
            return resolveWhatsApp(message) != null;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public WhatsAppCloudHealthNotificationRuntimeConfiguration resolveWhatsApp(HealthNotificationMessage message) {
        if (message == null || message.tenantId() == null) return null;
        WhatsAppCloudHealthNotificationConfiguration configuration = repository.findEnabledWhatsAppConfiguration(message.tenantId());
        if (configuration == null || !configuration.isAccessTokenConfigured() || configuration.getPhoneNumberId() == null) return null;
        WhatsAppHealthNotificationTemplate template = repository.findBestWhatsAppTemplate(
                configuration.getId(), firstEventType(message), message.deliveryMode(), message.locale());
        String templateName = template == null ? configuration.getDefaultTemplateName() : template.getTemplateName();
        String languageCode = template == null ? configuration.getDefaultLanguageCode() : template.getLanguageCode();
        if (templateName == null || templateName.isBlank()) return null;
        List<WhatsAppHealthNotificationRuntimeParameter> parameters = template == null
                ? List.of()
                : template.getParameters().stream().map(parameter -> new WhatsAppHealthNotificationRuntimeParameter(
                        parameter.getComponentType(),
                        parameter.getButtonSubType(),
                        parameter.getButtonIndex(),
                        parameter.getParameterOrder(),
                        resolveParameter(parameter, message))).toList();
        return new WhatsAppCloudHealthNotificationRuntimeConfiguration(
                configuration.getId(),
                configuration.getApiBaseUrl(),
                configuration.getGraphApiVersion(),
                configuration.getPhoneNumberId(),
                configuration.getWhatsAppBusinessAccountId(),
                decrypt(WHATSAPP_PROVIDER, configuration, configuration.getEncryptedAccessToken()),
                templateName,
                languageCode == null || languageCode.isBlank() ? "en_US" : languageCode,
                parameters);
    }

    private com.wizzdi.basic.iot.model.HealthNotificationEventType firstEventType(HealthNotificationMessage message) {
        if (message.items() == null || message.items().isEmpty()) return null;
        com.wizzdi.basic.iot.model.HealthNotificationEventType first = message.items().get(0).eventType();
        return message.items().stream().allMatch(item -> item.eventType() == first) ? first : null;
    }

    private String resolveParameter(WhatsAppHealthNotificationTemplateParameter parameter,
                                    HealthNotificationMessage message) {
        HealthNotificationItem first = message.items() == null || message.items().isEmpty()
                ? null : message.items().get(0);
        String value = switch (parameter.getValueSource()) {
            case LITERAL -> parameter.getLiteralValue();
            case SUBJECT -> message.subject();
            case BODY -> message.body();
            case DELIVERY_MODE -> message.deliveryMode() == null ? null : message.deliveryMode().name();
            case ITEM_COUNT -> Integer.toString(message.items() == null ? 0 : message.items().size());
            case SUMMARY_LINES -> message.items() == null ? null : message.items().stream()
                    .map(item -> item.title() + ": " + item.message()).reduce((a, b) -> a + "\n" + b).orElse(null);
            case FIRST_ITEM_TITLE -> first == null ? null : first.title();
            case FIRST_ITEM_MESSAGE -> first == null ? null : first.message();
            case FIRST_ITEM_SEVERITY_NAME -> first == null ? null : first.severityName();
            case FIRST_ITEM_SEVERITY_VALUE -> first == null || first.severityValue() == null
                    ? null : first.severityValue().toString();
            case FIRST_ITEM_OCCURRED_AT -> formatOccurredAt(first, parameter);
            case FIRST_ITEM_EVENT_TYPE -> first == null || first.eventType() == null ? null : first.eventType().name();
            case FIRST_ITEM_REMOTE_ID -> first == null ? null : first.remoteId();
            case FIRST_ITEM_REMOTE_GROUP_ID -> first == null ? null : first.remoteGroupId();
            case FIRST_ITEM_INCIDENT_ID -> first == null ? null : first.incidentId();
            case USER_ID -> message.userId();
            case DESTINATION -> message.destination();
            case TENANT_ID -> message.tenantId();
        };
        if (value == null || value.isBlank()) value = parameter.getFallbackValue();
        return value == null ? "" : value;
    }

    private String formatOccurredAt(HealthNotificationItem first,
                                    WhatsAppHealthNotificationTemplateParameter parameter) {
        if (first == null || first.occurredAt() == null) return null;
        ZoneId zone = ZoneId.of(parameter.getTimeZone() == null ? "UTC" : parameter.getTimeZone());
        String pattern = parameter.getDateTimePattern() == null || parameter.getDateTimePattern().isBlank()
                ? "yyyy-MM-dd HH:mm:ss XXX" : parameter.getDateTimePattern();
        return first.occurredAt().atZoneSameInstant(zone).format(DateTimeFormatter.ofPattern(pattern));
    }

    private byte[] encrypt(String provider, Baseclass configuration, String secret) {
        try {
            return encryptionService.encrypt(secret.getBytes(StandardCharsets.UTF_8), associatedData(provider, configuration));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed encrypting " + provider + " credentials", e);
        }
    }

    private String decrypt(String provider, Baseclass configuration, byte[] encrypted) {
        try {
            return new String(encryptionService.decrypt(encrypted, associatedData(provider, configuration)), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed decrypting " + provider + " credentials", e);
        }
    }

    private byte[] associatedData(String provider, Baseclass configuration) {
        String tenantId = configuration.getTenant() == null ? "" : configuration.getTenant().getId();
        return (provider + "\u0000" + tenantId + "\u0000" + configuration.getId()).getBytes(StandardCharsets.UTF_8);
    }

    private String fingerprint(String secret) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest, 0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private void validateProviderUrl(String value, Set<String> defaults) {
        URI uri;
        try {
            uri = URI.create(normalizeBaseUrl(value));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid provider API base URL");
        }
        if (!allowInsecureEndpoints && !"https".equalsIgnoreCase(uri.getScheme())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provider API base URL must use HTTPS");
        }
        String origin = uri.getScheme() + "://" + uri.getAuthority();
        if (!allowCustomEndpoints && defaults.stream().noneMatch(origin::equalsIgnoreCase)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Custom provider endpoints are disabled: " + origin);
        }
    }

    private String normalizeBaseUrl(String value) {
        String normalized = value == null ? null : value.trim();
        while (normalized != null && normalized.endsWith("/")) normalized = normalized.substring(0, normalized.length() - 1);
        return normalized;
    }

    private String normalizeLocale(String locale) {
        return locale == null || locale.isBlank() ? "en" : locale.trim().replace('-', '_').toLowerCase(Locale.ROOT);
    }

    private void validateLocale(String locale) {
        if (locale != null && !locale.isBlank() && !locale.matches("[A-Za-z]{2,3}([_-][A-Za-z]{2,4})?")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid locale " + locale);
        }
    }

    private String value(String requested, String existing) {
        return requested == null || requested.isBlank() ? existing : requested;
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

}
