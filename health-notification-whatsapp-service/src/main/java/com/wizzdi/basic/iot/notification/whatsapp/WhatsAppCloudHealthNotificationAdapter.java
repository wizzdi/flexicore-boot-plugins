package com.wizzdi.basic.iot.notification.whatsapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizzdi.basic.iot.model.HealthNotificationChannel;
import com.wizzdi.basic.iot.model.WhatsAppTemplateComponentType;
import com.wizzdi.basic.iot.service.notification.HealthNotificationChannelAdapter;
import com.wizzdi.basic.iot.service.notification.HealthNotificationMessage;
import com.wizzdi.basic.iot.service.notification.HealthNotificationProviderUnavailableException;
import com.wizzdi.basic.iot.service.notification.HealthNotificationSendResult;
import com.wizzdi.basic.iot.service.notification.WhatsAppCloudHealthNotificationRuntimeConfiguration;
import com.wizzdi.basic.iot.service.notification.WhatsAppHealthNotificationRuntimeParameter;
import com.wizzdi.basic.iot.service.service.HealthNotificationProviderConfigurationService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Extension
@Component
public class WhatsAppCloudHealthNotificationAdapter implements HealthNotificationChannelAdapter {
    private static final int MAX_PROVIDER_RESPONSE_LENGTH = 2000;

    @Autowired
    private HealthNotificationProviderConfigurationService configurationService;
    @Autowired
    private ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    @Override
    public HealthNotificationChannel channel() {
        return HealthNotificationChannel.WHATSAPP;
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public boolean supports(HealthNotificationMessage message) {
        return message != null
                && message.channel() == HealthNotificationChannel.WHATSAPP
                && configurationService.canResolveWhatsApp(message);
    }

    @Override
    public HealthNotificationSendResult send(HealthNotificationMessage message) throws Exception {
        WhatsAppCloudHealthNotificationRuntimeConfiguration configuration = configurationService.resolveWhatsApp(message);
        if (configuration == null) {
            throw new HealthNotificationProviderUnavailableException(
                    "No enabled WhatsApp Cloud configuration and approved template are available for tenant " + message.tenantId());
        }
        byte[] payload = objectMapper.writeValueAsBytes(buildPayload(configuration, message));
        String endpoint = configuration.apiBaseUrl() + "/" + configuration.graphApiVersion()
                + "/" + configuration.phoneNumberId() + "/messages";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(45))
                .header("Authorization", "Bearer " + configuration.accessToken())
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofByteArray(payload))
                .build();
        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        String responseBody = truncate(response.body());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("WhatsApp Cloud API returned HTTP " + response.statusCode() + ": " + responseBody);
        }
        return new HealthNotificationSendResult(extractMessageId(response.body()), response.statusCode(), responseBody);
    }

    private Map<String, Object> buildPayload(WhatsAppCloudHealthNotificationRuntimeConfiguration configuration,
                                             HealthNotificationMessage message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("recipient_type", "individual");
        payload.put("to", normalizePhone(message.destination()));
        payload.put("type", "template");

        Map<String, Object> template = new LinkedHashMap<>();
        template.put("name", configuration.templateName());
        template.put("language", Map.of("code", configuration.languageCode()));
        List<Map<String, Object>> components = components(configuration.parameters());
        if (!components.isEmpty()) template.put("components", components);
        payload.put("template", template);
        return payload;
    }

    private List<Map<String, Object>> components(List<WhatsAppHealthNotificationRuntimeParameter> parameters) {
        if (parameters == null || parameters.isEmpty()) return List.of();
        List<WhatsAppHealthNotificationRuntimeParameter> sorted = parameters.stream()
                .sorted(Comparator.comparing(WhatsAppHealthNotificationRuntimeParameter::componentType)
                        .thenComparingInt(WhatsAppHealthNotificationRuntimeParameter::buttonIndex)
                        .thenComparingInt(WhatsAppHealthNotificationRuntimeParameter::parameterOrder))
                .toList();
        Map<ComponentKey, List<WhatsAppHealthNotificationRuntimeParameter>> grouped = new LinkedHashMap<>();
        for (WhatsAppHealthNotificationRuntimeParameter parameter : sorted) {
            ComponentKey key = new ComponentKey(parameter.componentType(), parameter.buttonSubType(), parameter.buttonIndex());
            grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(parameter);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<ComponentKey, List<WhatsAppHealthNotificationRuntimeParameter>> entry : grouped.entrySet()) {
            ComponentKey key = entry.getKey();
            Map<String, Object> component = new LinkedHashMap<>();
            component.put("type", key.componentType().name().toLowerCase());
            if (key.componentType() == WhatsAppTemplateComponentType.BUTTON) {
                if (key.buttonSubType() == null) {
                    throw new IllegalStateException("WhatsApp button template component is missing buttonSubType");
                }
                component.put("sub_type", key.buttonSubType().name().toLowerCase());
                component.put("index", Integer.toString(key.buttonIndex()));
            }
            component.put("parameters", entry.getValue().stream()
                    .map(parameter -> whatsappParameter(key, parameter.value()))
                    .toList());
            result.add(component);
        }
        return result;
    }


    private Map<String, Object> whatsappParameter(ComponentKey key, String value) {
        if (key.componentType() == WhatsAppTemplateComponentType.BUTTON
                && key.buttonSubType() == com.wizzdi.basic.iot.model.WhatsAppTemplateButtonSubType.QUICK_REPLY) {
            return Map.of("type", "payload", "payload", value == null ? "" : value);
        }
        return Map.of("type", "text", "text", value == null ? "" : value);
    }

    private String normalizePhone(String destination) {
        if (destination == null) return null;
        String normalized = destination.replaceAll("[^0-9]", "");
        if (normalized.isBlank()) throw new IllegalArgumentException("WhatsApp destination contains no digits");
        return normalized;
    }

    private String extractMessageId(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode messages = root.path("messages");
            if (messages.isArray() && !messages.isEmpty()) {
                return messages.get(0).path("id").asText(null);
            }
        } catch (Exception ignored) {
            // Provider response remains stored for diagnostics even when the message id cannot be parsed.
        }
        return null;
    }

    private String truncate(String value) {
        if (value == null || value.length() <= MAX_PROVIDER_RESPONSE_LENGTH) return value;
        return value.substring(0, MAX_PROVIDER_RESPONSE_LENGTH);
    }

    private record ComponentKey(
            WhatsAppTemplateComponentType componentType,
            com.wizzdi.basic.iot.model.WhatsAppTemplateButtonSubType buttonSubType,
            int buttonIndex) {
        private ComponentKey {
            Objects.requireNonNull(componentType, "componentType");
        }
    }
}
