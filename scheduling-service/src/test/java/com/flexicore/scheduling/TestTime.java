package com.flexicore.scheduling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.*;

public class TestTime {

    @Test
    public void testTime() {
        OffsetDateTime offsetDateTime=OffsetDateTime.parse("2023-11-12T18:38:06.57+01");//=== 2023-11-12T17:38:06.57+00 === 2023-11-12T19:38:06.57+02
        OffsetTime offsetTime = getOffSetTime1(offsetDateTime, "Asia/Jerusalem");
        OffsetTime offsetTime2 = getOffSetTime2(offsetDateTime, "Asia/Jerusalem");
        Assertions.assertEquals(offsetTime,offsetTime2);
    }


    public static OffsetTime getOffSetTime2(OffsetDateTime startTime, String timezoneId) {


        ZonedDateTime zonedDateTime1 = startTime.atZoneSameInstant(ZoneId.of(timezoneId));
        System.out.println("zone:"+zonedDateTime1);

        OffsetTime offsetTime = zonedDateTime1.toOffsetDateTime().toOffsetTime();
        System.out.println("offsettime:"+offsetTime);

        return offsetTime;

    }
    public static OffsetTime getOffSetTime1(OffsetDateTime startTime, String timezoneId) {

        ZonedDateTime zonedStartTime = startTime.atZoneSameInstant(startTime.getOffset());
        System.out.println("zone:"+zonedStartTime);

        ZoneId zoneId = ZoneId.of(timezoneId);
        LocalDate currentDate = LocalDate.now(zoneId);


        ZonedDateTime StartZonedDateTime = ZonedDateTime.of(currentDate, zonedStartTime.toLocalTime(), zoneId);
        System.out.println("zone2:"+StartZonedDateTime);


        OffsetTime offsetTime = StartZonedDateTime.toOffsetDateTime().toOffsetTime();
        System.out.println("offsettime:"+offsetTime);

        return offsetTime;
    }
}
