package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.SendGridHealthNotificationConfiguration;
import com.wizzdi.basic.iot.model.SendGridHealthNotificationTemplate;
import com.wizzdi.basic.iot.model.WhatsAppCloudHealthNotificationConfiguration;
import com.wizzdi.basic.iot.model.WhatsAppHealthNotificationTemplate;
import com.wizzdi.basic.iot.service.request.HealthNotificationProviderConfigurationFilter;
import com.wizzdi.basic.iot.service.request.HealthNotificationProviderTemplateFilter;
import com.wizzdi.basic.iot.service.request.SendGridHealthNotificationConfigurationCreate;
import com.wizzdi.basic.iot.service.request.SendGridHealthNotificationConfigurationUpdate;
import com.wizzdi.basic.iot.service.request.SendGridHealthNotificationTemplateCreate;
import com.wizzdi.basic.iot.service.request.SendGridHealthNotificationTemplateUpdate;
import com.wizzdi.basic.iot.service.request.WhatsAppCloudHealthNotificationConfigurationCreate;
import com.wizzdi.basic.iot.service.request.WhatsAppCloudHealthNotificationConfigurationUpdate;
import com.wizzdi.basic.iot.service.request.WhatsAppHealthNotificationTemplateCreate;
import com.wizzdi.basic.iot.service.request.WhatsAppHealthNotificationTemplateUpdate;
import com.wizzdi.basic.iot.service.service.HealthNotificationProviderConfigurationService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@OperationsInside
@RequestMapping("/plugins/HealthNotificationProviders")
@Tag(name = "HealthNotificationProviders")
@Extension
@RestController
public class HealthNotificationProviderController implements Plugin, Invoker {
    @Autowired
    private HealthNotificationProviderConfigurationService service;

    @PostMapping("/getAllSendGridConfigurations")
    @Operation(summary = "getAllSendGridConfigurations")
    public PaginationResponse<SendGridHealthNotificationConfiguration> getAllSendGridConfigurations(
            @RequestBody HealthNotificationProviderConfigurationFilter filter,
            @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAllSendGridConfigurations(securityContext, filter);
    }

    @PostMapping("/createSendGridConfiguration")
    @Operation(summary = "createSendGridConfiguration")
    public SendGridHealthNotificationConfiguration createSendGridConfiguration(
            @RequestBody SendGridHealthNotificationConfigurationCreate create,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(create, securityContext);
        return service.createSendGrid(create, securityContext);
    }

    @PutMapping("/updateSendGridConfiguration")
    @Operation(summary = "updateSendGridConfiguration")
    public SendGridHealthNotificationConfiguration updateSendGridConfiguration(
            @RequestBody SendGridHealthNotificationConfigurationUpdate update,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(update, securityContext);
        return service.updateSendGrid(update, securityContext);
    }

    @PostMapping("/getAllWhatsAppCloudConfigurations")
    @Operation(summary = "getAllWhatsAppCloudConfigurations")
    public PaginationResponse<WhatsAppCloudHealthNotificationConfiguration> getAllWhatsAppConfigurations(
            @RequestBody HealthNotificationProviderConfigurationFilter filter,
            @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAllWhatsAppConfigurations(securityContext, filter);
    }

    @PostMapping("/createWhatsAppCloudConfiguration")
    @Operation(summary = "createWhatsAppCloudConfiguration")
    public WhatsAppCloudHealthNotificationConfiguration createWhatsAppConfiguration(
            @RequestBody WhatsAppCloudHealthNotificationConfigurationCreate create,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(create, securityContext);
        return service.createWhatsApp(create, securityContext);
    }

    @PutMapping("/updateWhatsAppCloudConfiguration")
    @Operation(summary = "updateWhatsAppCloudConfiguration")
    public WhatsAppCloudHealthNotificationConfiguration updateWhatsAppConfiguration(
            @RequestBody WhatsAppCloudHealthNotificationConfigurationUpdate update,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(update, securityContext);
        return service.updateWhatsApp(update, securityContext);
    }

    @PostMapping("/getAllSendGridTemplates")
    @Operation(summary = "getAllSendGridTemplates")
    public PaginationResponse<SendGridHealthNotificationTemplate> getAllSendGridTemplates(
            @RequestBody HealthNotificationProviderTemplateFilter filter,
            @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAllSendGridTemplates(securityContext, filter);
    }

    @PostMapping("/createSendGridTemplate")
    @Operation(summary = "createSendGridTemplate")
    public SendGridHealthNotificationTemplate createSendGridTemplate(
            @RequestBody SendGridHealthNotificationTemplateCreate create,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(create, securityContext);
        return service.createSendGridTemplate(create, securityContext);
    }

    @PutMapping("/updateSendGridTemplate")
    @Operation(summary = "updateSendGridTemplate")
    public SendGridHealthNotificationTemplate updateSendGridTemplate(
            @RequestBody SendGridHealthNotificationTemplateUpdate update,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(update, securityContext);
        return service.updateSendGridTemplate(update, securityContext);
    }

    @PostMapping("/getAllWhatsAppTemplates")
    @Operation(summary = "getAllWhatsAppTemplates")
    public PaginationResponse<WhatsAppHealthNotificationTemplate> getAllWhatsAppTemplates(
            @RequestBody HealthNotificationProviderTemplateFilter filter,
            @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAllWhatsAppTemplates(securityContext, filter);
    }

    @PostMapping("/createWhatsAppTemplate")
    @Operation(summary = "createWhatsAppTemplate")
    public WhatsAppHealthNotificationTemplate createWhatsAppTemplate(
            @RequestBody WhatsAppHealthNotificationTemplateCreate create,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(create, securityContext);
        return service.createWhatsAppTemplate(create, securityContext);
    }

    @PutMapping("/updateWhatsAppTemplate")
    @Operation(summary = "updateWhatsAppTemplate")
    public WhatsAppHealthNotificationTemplate updateWhatsAppTemplate(
            @RequestBody WhatsAppHealthNotificationTemplateUpdate update,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(update, securityContext);
        return service.updateWhatsAppTemplate(update, securityContext);
    }

    @Override
    public Class<?> getHandlingClass() {
        return SendGridHealthNotificationConfiguration.class;
    }
}
