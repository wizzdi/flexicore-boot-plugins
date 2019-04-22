package com.flexicore.scheduling.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flexicore.data.jsoncontainers.*;
import com.flexicore.scheduling.containers.request.UpdateTimeslot;
import com.flexicore.scheduling.model.ScheduleTimeslot;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.calendar.astro.SolarTime;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Test {

    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper=new ObjectMapper();

        configureObjectMapper(Test.class.getClassLoader(),objectMapper);
        UpdateTimeslot updateTimeslot=objectMapper.readValue("{\n" +
                " \n" +
                "  \"timeOfTheDayStart\": \"2019-04-22T05:04:00+03:00\",\n" +
                "  \"timeOfTheDayEnd\": \"2019-04-21T23:59:30+03:00\",\n" +
                "  \"scheduleTimeslotId\": \"eICvd5ECS1KiJR5jfWGbAQ\"\n" +
                "}", UpdateTimeslot.class);
        ScheduleTimeslot scheduleTimeslot=new ScheduleTimeslot().setStartTime(updateTimeslot.getTimeOfTheDayStart().toLocalDateTime());
        System.out.println(objectMapper.writeValueAsString(scheduleTimeslot));

    }

    private static ObjectMapper configureObjectMapper(ClassLoader classLoader,  ObjectMapper mapper) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

        SimpleModule mod = new SimpleModule();
        FieldSetContainerDeserializer<? extends Serializable> s = new FieldSetContainerDeserializer();
        mod.addDeserializer(FieldSetContainer.class, s);
        PropertySetContainerDeserializer<? extends Serializable> s1 = new PropertySetContainerDeserializer();
        mod.addDeserializer(PropertySetContainer.class, s1);
        mapper.registerModule(mod);
        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);
        SimpleModule dateModule = new SimpleModule("date module");
        dateModule.addDeserializer(LocalDateTime.class, new JsonDateDeserializer("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        dateModule.addSerializer(LocalDateTime.class, new JsonDateSerializer());
        mapper.registerModule(dateModule);
        if (classLoader != null) {
            TypeFactory tf = TypeFactory.defaultInstance().withClassLoader(classLoader);
            mapper.setTypeFactory(tf);
            System.out.println("Created Object Mapper For " + classLoader);
        } else {
            System.out.println("Created Default Object Mapper");
        }

        return mapper;
    }
}
