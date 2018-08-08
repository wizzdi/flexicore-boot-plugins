package com.flexicore.scheduling.service;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.calendar.astro.SolarTime;

import java.util.Optional;

public class Test {

    public static void main(String[] args){
        SolarTime solarTime=SolarTime.ofLocation(31.260828, 34.798089);
        Optional<Moment> result = PlainDate.nowInSystemTime().get(solarTime.sunrise());
        System.out.println(result.get());
    }
}
