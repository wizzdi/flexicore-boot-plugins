package com.flexicore.scheduling;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.request.ScheduleCreate;
import com.flexicore.scheduling.service.ScheduleService;
import com.flexicore.security.SecurityContextBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class AppConfig {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    @Qualifier("adminSecurityContext")
    private SecurityContextBase securityContext;

    @Bean
    public Schedule schedule() {
        ScheduleCreate scheduleCreate = new ScheduleCreate();
        return scheduleService.createSchedule(scheduleCreate, securityContext);
    }



}
