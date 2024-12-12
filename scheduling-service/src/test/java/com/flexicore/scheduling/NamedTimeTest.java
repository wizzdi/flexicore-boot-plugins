package com.flexicore.scheduling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flexicore.scheduling.model.ScheduleTimeslot;
import com.flexicore.scheduling.model.TimeOfTheDayName;
import com.flexicore.scheduling.service.Scheduler;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.calendar.astro.SolarTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

public class NamedTimeTest {
    private static final ObjectMapper objectMapper=new ObjectMapper().registerModule(new JavaTimeModule()).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    @Test
    public void testSunrise() throws JsonProcessingException {
        ZoneOffset cetOffset = ZoneId.of("CET").getRules().getOffset(Instant.now());
        OffsetDateTime now=OffsetDateTime.of(2024,12,12,5,33,33,634, cetOffset);
        ScheduleTimeslot timeslot = objectMapper.readValue(json,ScheduleTimeslot.class);
        boolean time = Scheduler.isTime(timeslot, now);
        Assertions.assertTrue(time);
    }


    public Optional<OffsetTime> getTimeFromName(
            TimeOfTheDayName timeOfTheDayName, double lon, double lat) {
        SolarTime solarTime;
        Optional<Moment> result = switch (timeOfTheDayName) {
            case SUNRISE -> {
                solarTime = SolarTime.ofLocation(lat, lon);
                yield PlainDate.nowInSystemTime().get(solarTime.sunrise());
            }
            case SUNSET -> {
                solarTime = SolarTime.ofLocation(lat, lon);
                yield PlainDate.nowInSystemTime().get(solarTime.sunset());
            }
        };

        return result.map(f -> f.toLocalTimestamp().toTemporalAccessor()
                .atZone(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime());

    }

    private static String json= """
         {
            "json-id": "85dd7245-b4f7-4374-8c0c-5de2e78e2444",
            "id": "78a6dac8-cb21-4e36-a007-946f8f8d6416",
            "name": "OFF_TS",
            "description": null,
            "softDelete": false,
            "creationDate": "2024-05-15T14:41:46.032299+02:00",
            "updateDate": "2024-12-11T08:05:51.897079+01:00",
            "startTimeOfTheDayName": "SUNRISE",
            "startTime": "06:29:00",
            "lastExecution": "2024-12-09T05:33:44.821871+01:00",
            "log": "Time slot is NOT  effective now: 2024-11-19T14:36:35.600083842+01:00 , in time frame: false , previous run: false",
            "startMillisOffset": -15000,
            "endTimeOfTheDayName": "SUNRISE",
            "timeOfTheDayNameEndLon": 35.004661004674375,
            "timeOfTheDayNameEndLat": 32.78158065774335,
            "endTime": "06:34:00",
            "coolDownIntervalBeforeRepeat": 180565,
            "endMillisOffset": -10000,
            "schedule": {
                "json-id": "26eaf745-a3f4-4bff-8488-b88639b91241",
                "id": "5c2f60ea-189e-4d25-8403-e916fa78cd8f",
                "name": "XXXX",
                "description": null,
                "softDelete": false,
                "creationDate": "2024-05-15T14:39:16.121723+02:00",
                "updateDate": "2024-12-11T08:06:02.943027+01:00",
                "enabled": true,
                "monday": true,
                "saturday": true,
                "tuesday": true,
                "holiday": false,
                "sunday": true,
                "thursday": true,
                "wednesday": true,
                "friday": true,
                "timeFrameStart": "2024-05-14T23:00:00+02:00",
                "timeFrameEnd": "2050-05-20T23:00:00+02:00",
                "selectedTimeZone": "Asia/Jerusalem",
                "log": null,
                "javaType": "com.flexicore.scheduling.model.Schedule",
                "json-type": "com.flexicore.scheduling.model.Schedule"
            },
            "timeOfTheDayNameStartLat": 32.78158065774335,
            "timeOfTheDayNameStartLon": 35.004661004674375,
            "javaType": "com.flexicore.scheduling.model.ScheduleTimeslot",
            "json-type": "com.flexicore.scheduling.model.ScheduleTimeslot"
        }
            """;
}
