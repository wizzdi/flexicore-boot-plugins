package com.flexicore.scheduling;

import com.flexicore.scheduling.service.Scheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

public class TimeOffsetTests {
    @Test
    public void testTimeSummer(){
        OffsetDateTime now=OffsetDateTime.of(2024,10,26,10,0,0,0, ZoneOffset.UTC);
        String zoneId="Israel";
        LocalTime localTime=LocalTime.of(14,0);
        OffsetTime offSetTime = Scheduler.getOffSetTime(now, localTime, zoneId);
        Assertions.assertEquals(14,offSetTime.getHour());
        Assertions.assertEquals(3*60*60,offSetTime.getOffset().getTotalSeconds());
        System.out.println(offSetTime);
    }

    @Test
    public void testTimeWinter(){
        OffsetDateTime now=OffsetDateTime.of(2024,10,29,10,0,0,0, ZoneOffset.UTC);
        String zoneId="Israel";
        LocalTime localTime=LocalTime.of(14,0);
        OffsetTime offSetTime = Scheduler.getOffSetTime(now, localTime, zoneId);
        Assertions.assertEquals(14,offSetTime.getHour());
        Assertions.assertEquals(2*60*60,offSetTime.getOffset().getTotalSeconds());
        System.out.println(offSetTime);
    }
}
