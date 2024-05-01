package com.wizzdi.maps.service.response;

import java.time.OffsetDateTime;

public record StatusHistoryContainer(String id, OffsetDateTime dateAtStatus,String name) {
}
