package com.flexicore.scheduling;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.calendar.astro.SolarTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.Optional;

public class NamedTimeTest {
    @Test
    public void testSunrise(){
        SolarTime solarTime = SolarTime.ofLocation(31.925463281549824, 34.9778810406676);
        Optional<Moment> result= PlainDate.nowInSystemTime().get(solarTime.sunrise());
        Optional<OffsetTime> offsetTime = result.map(f -> f.toLocalTimestamp().toTemporalAccessor()
                .atZone(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime());
        Assertions.assertTrue(offsetTime.isPresent());
        OffsetTime offsetTimeRes = offsetTime.get();
        System.out.println(offsetTimeRes);
    }
}
