package com.wizzdi.basic.iot.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizzdi.basic.iot.client.WifiAccessPointInfo;
import com.wizzdi.basic.iot.service.response.GatewayLocationResolution;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Extension
@Component
public class GoogleGeolocationService implements Plugin {

    public static final String LOCATION_SOURCE_WIFI_GOOGLE = "WIFI_GOOGLE";
    public static final String LOCATION_SOURCE_GATEWAY_REPORTED = "GATEWAY_REPORTED";

    private static final Logger logger = LoggerFactory.getLogger("basic-iot");

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${basic.iot.google.geolocation.apiKey:${GOOGLE_GEOLOCATION_API_KEY:}}")
    private String apiKey;

    @Value("${basic.iot.google.geolocation.url:https://www.googleapis.com/geolocation/v1/geolocate}")
    private String geolocationUrl;

    @Value("${basic.iot.google.geolocation.timeoutSeconds:10}")
    private int timeoutSeconds;

    @Value("${basic.iot.google.geolocation.considerIp:false}")
    private boolean considerIp;

    /**
     * Resolves a Wi-Fi fingerprint with Google Geolocation API.
     * Returns empty when no key is configured, fewer than two usable BSSIDs are present,
     * or Google cannot resolve the fingerprint.
     */
    public Optional<GatewayLocationResolution> resolveWifiLocation(List<WifiAccessPointInfo> wifiAccessPoints) {
        List<WifiAccessPointInfo> normalized = normalize(wifiAccessPoints);
        if (normalized.size() < 2) {
            logger.debug("skipping Wi-Fi geolocation; need at least two usable BSSIDs, got {}", normalized.size());
            return Optional.empty();
        }
        if (apiKey == null || apiKey.isBlank()) {
            logger.debug("skipping Wi-Fi geolocation; basic.iot.google.geolocation.apiKey is not configured");
            return Optional.empty();
        }
        try {
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("considerIp", considerIp);
            List<Map<String, Object>> googleAccessPoints = new ArrayList<>();
            for (WifiAccessPointInfo accessPoint : normalized) {
                Map<String, Object> googleAccessPoint = new LinkedHashMap<>();
                googleAccessPoint.put("macAddress", accessPoint.getMacAddress());
                googleAccessPoint.put("signalStrength", accessPoint.getSignalStrength());
                if (accessPoint.getChannel() != null) {
                    googleAccessPoint.put("channel", accessPoint.getChannel());
                }
                googleAccessPoints.add(googleAccessPoint);
            }
            requestBody.put("wifiAccessPoints", googleAccessPoints);
            String payload = objectMapper.writeValueAsString(requestBody);
            String encodedKey = URLEncoder.encode(apiKey.trim(), StandardCharsets.UTF_8);
            URI uri = URI.create(geolocationUrl + "?key=" + encodedKey);
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                logger.warn("Google Wi-Fi geolocation failed status={} body={}", response.statusCode(), response.body());
                return Optional.empty();
            }
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode location = root.path("location");
            if (!location.path("lat").isNumber() || !location.path("lng").isNumber()) {
                logger.warn("Google Wi-Fi geolocation response did not contain location: {}", response.body());
                return Optional.empty();
            }
            GatewayLocationResolution resolution = new GatewayLocationResolution()
                    .setLat(location.path("lat").asDouble())
                    .setLon(location.path("lng").asDouble())
                    .setAccuracyMeters(root.path("accuracy").isNumber() ? root.path("accuracy").asDouble() : null)
                    .setSource(LOCATION_SOURCE_WIFI_GOOGLE);
            logger.info("resolved gateway location from Wi-Fi fingerprint lat={} lon={} accuracyMeters={} aps={}",
                    resolution.getLat(), resolution.getLon(), resolution.getAccuracyMeters(), normalized.size());
            return Optional.of(resolution);
        }
        catch (IOException e) {
            logger.warn("failed calling Google Wi-Fi geolocation", e);
            return Optional.empty();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("interrupted while calling Google Wi-Fi geolocation", e);
            return Optional.empty();
        }
        catch (Exception e) {
            logger.warn("unexpected error resolving Wi-Fi location", e);
            return Optional.empty();
        }
    }

    public String toWifiAccessPointsJson(List<WifiAccessPointInfo> wifiAccessPoints) {
        List<WifiAccessPointInfo> normalized = normalize(wifiAccessPoints);
        if (normalized.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(normalized);
        }
        catch (JsonProcessingException e) {
            logger.warn("failed serializing Wi-Fi access-point fingerprint", e);
            return null;
        }
    }

    public List<WifiAccessPointInfo> normalize(List<WifiAccessPointInfo> wifiAccessPoints) {
        if (wifiAccessPoints == null || wifiAccessPoints.isEmpty()) {
            return List.of();
        }
        Map<String, WifiAccessPointInfo> byMac = new LinkedHashMap<>();
        for (WifiAccessPointInfo accessPoint : wifiAccessPoints) {
            if (accessPoint == null) {
                continue;
            }
            String macAddress = normalizeMac(accessPoint.getMacAddress());
            Integer signalStrength = accessPoint.getSignalStrength();
            if (macAddress == null || signalStrength == null || signalStrength > -10 || signalStrength < -128) {
                continue;
            }
            WifiAccessPointInfo normalized = new WifiAccessPointInfo()
                    .setMacAddress(macAddress)
                    .setSignalStrength(signalStrength)
                    .setChannel(accessPoint.getChannel())
                    .setFrequencyMhz(accessPoint.getFrequencyMhz());
            WifiAccessPointInfo existing = byMac.get(macAddress);
            if (existing == null || signalStrength > existing.getSignalStrength()) {
                byMac.put(macAddress, normalized);
            }
        }
        return byMac.values().stream()
                .sorted((a, b) -> Integer.compare(b.getSignalStrength(), a.getSignalStrength()))
                .limit(20)
                .toList();
    }

    private String normalizeMac(String macAddress) {
        if (macAddress == null) {
            return null;
        }
        String normalized = macAddress.trim().toLowerCase(Locale.ROOT);
        return normalized.matches("[0-9a-f]{2}(:[0-9a-f]{2}){5}") ? normalized : null;
    }
}
