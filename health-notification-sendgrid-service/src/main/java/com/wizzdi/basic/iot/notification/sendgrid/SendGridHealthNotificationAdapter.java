package com.wizzdi.basic.iot.notification.sendgrid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizzdi.basic.iot.model.HealthNotificationChannel;
import com.wizzdi.basic.iot.service.notification.HealthNotificationChannelAdapter;
import com.wizzdi.basic.iot.service.notification.HealthNotificationItem;
import com.wizzdi.basic.iot.service.notification.HealthNotificationMessage;
import com.wizzdi.basic.iot.service.notification.HealthNotificationProviderUnavailableException;
import com.wizzdi.basic.iot.service.notification.HealthNotificationSendResult;
import com.wizzdi.basic.iot.service.notification.SendGridHealthNotificationRuntimeConfiguration;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Extension
@Component
public class SendGridHealthNotificationAdapter implements HealthNotificationChannelAdapter {
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
        return HealthNotificationChannel.EMAIL;
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public boolean supports(HealthNotificationMessage message) {
        return message != null
                && message.channel() == HealthNotificationChannel.EMAIL
                && configurationService.canResolveSendGrid(message);
    }

    @Override
    public HealthNotificationSendResult send(HealthNotificationMessage message) throws Exception {
        SendGridHealthNotificationRuntimeConfiguration configuration = configurationService.resolveSendGrid(message);
        if (configuration == null) {
            throw new HealthNotificationProviderUnavailableException(
                    "No enabled SendGrid configuration and template are available for tenant " + message.tenantId());
        }
        byte[] payload = objectMapper.writeValueAsBytes(buildPayload(configuration, message));
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(configuration.apiBaseUrl() + "/v3/mail/send"))
                .timeout(Duration.ofSeconds(45))
                .header("Authorization", "Bearer " + configuration.apiKey())
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofByteArray(payload));
        if (configuration.onBehalfOf() != null && !configuration.onBehalfOf().isBlank()) {
            requestBuilder.header("on-behalf-of", configuration.onBehalfOf());
        }
        HttpResponse<String> response = httpClient.send(
                requestBuilder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        String responseBody = truncate(response.body());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("SendGrid returned HTTP " + response.statusCode() + ": " + responseBody);
        }
        String messageId = response.headers().firstValue("X-Message-Id")
                .or(() -> response.headers().firstValue("x-message-id"))
                .orElse(null);
        return new HealthNotificationSendResult(messageId, response.statusCode(), responseBody);
    }

    private Map<String, Object> buildPayload(SendGridHealthNotificationRuntimeConfiguration configuration,
                                             HealthNotificationMessage message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("from", emailAddress(configuration.fromEmail(), configuration.fromName()));
        if (configuration.replyToEmail() != null && !configuration.replyToEmail().isBlank()) {
            payload.put("reply_to", emailAddress(configuration.replyToEmail(), configuration.replyToName()));
        }

        Map<String, Object> personalization = new LinkedHashMap<>();
        personalization.put("to", List.of(emailAddress(message.destination(), null)));
        if (configuration.templateId() != null && !configuration.templateId().isBlank()) {
            personalization.put("dynamic_template_data", templateData(message));
            payload.put("template_id", configuration.templateId());
        } else {
            personalization.put("subject", message.subject());
            payload.put("subject", message.subject());
            List<Map<String, String>> content = new ArrayList<>();
            content.add(Map.of("type", "text/plain", "value", nullToEmpty(message.body())));
            content.add(Map.of("type", "text/html", "value", toHtml(message.body())));
            payload.put("content", content);
        }
        payload.put("personalizations", List.of(personalization));
        return payload;
    }

    private Map<String, Object> templateData(HealthNotificationMessage message) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("tenantId", message.tenantId());
        data.put("userId", message.userId());
        data.put("subject", message.subject());
        data.put("body", message.body());
        data.put("deliveryMode", message.deliveryMode() == null ? null : message.deliveryMode().name());
        data.put("locale", message.locale());
        data.put("itemCount", message.items() == null ? 0 : message.items().size());
        data.put("items", message.items() == null ? List.of() : message.items().stream().map(this::itemData).toList());
        return data;
    }

    private Map<String, Object> itemData(HealthNotificationItem item) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("deliveryId", item.deliveryId());
        data.put("eventId", item.eventId());
        data.put("eventType", item.eventType() == null ? null : item.eventType().name());
        data.put("incidentId", item.incidentId());
        data.put("remoteId", item.remoteId());
        data.put("remoteGroupId", item.remoteGroupId());
        data.put("title", item.title());
        data.put("message", item.message());
        data.put("severityName", item.severityName());
        data.put("severityValue", item.severityValue());
        data.put("occurredAt", item.occurredAt() == null ? null : item.occurredAt().toString());
        return data;
    }

    private Map<String, String> emailAddress(String email, String name) {
        Map<String, String> address = new LinkedHashMap<>();
        address.put("email", email);
        if (name != null && !name.isBlank()) address.put("name", name);
        return address;
    }

    private String toHtml(String value) {
        return "<div>" + escapeHtml(nullToEmpty(value)).replace("\n", "<br/>") + "</div>";
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String truncate(String value) {
        if (value == null || value.length() <= MAX_PROVIDER_RESPONSE_LENGTH) return value;
        return value.substring(0, MAX_PROVIDER_RESPONSE_LENGTH);
    }
}
