package com.flexicore.scheduling.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleTimeslot;
import com.flexicore.scheduling.model.TimeOfTheDayName;
import com.flexicore.scheduling.App;
import com.flexicore.scheduling.request.ScheduleTimeslotCreate;
import com.flexicore.scheduling.request.ScheduleTimeslotFilter;
import com.flexicore.scheduling.request.ScheduleTimeslotUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
public class ScheduleTimeslotControllerTest {

    private ScheduleTimeslot testScheduleTimeslot;
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Schedule schedule;

      private final static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:15")

          .withDatabaseName("flexicore-test")
          .withUsername("flexicore")
          .withPassword("flexicore");

  static {
    postgresqlContainer.start();
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresqlContainer::getUsername);
    registry.add("spring.datasource.password", postgresqlContainer::getPassword);
  }


  @BeforeAll
  public void init() {
    restTemplate.getRestTemplate().setInterceptors(
            Collections.singletonList((request, body, execution) -> {
              request.getHeaders()
                      .add("authenticationKey", "fake");
              return execution.execute(request, body);
            }));

  }

    @Test
    @Order(1)
    public void testScheduleTimeslotCreate() throws JsonProcessingException {
        String name = UUID.randomUUID().toString();
        ScheduleTimeslotCreate request = new ScheduleTimeslotCreate()
                .setStartTime(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/Bahia_Banderas")).toOffsetDateTime())
                .setStartMillisOffset(10L)
                .setScheduleId(this.schedule.getId())
                .setTimeOfTheDayNameEndLon(10D)
                .setTimeOfTheDayNameEndLat(10D)
                .setEndTime(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/Bahia_Banderas")).toOffsetDateTime())
                .setCoolDownIntervalBeforeRepeat(10L)
                .setEndMillisOffset(10L)
                .setName(name);
        request.setTimeOfTheDayNameStartLat(10D);

        request.setTimeOfTheDayNameStartLon(10D);
        ObjectMapper objectMapper = restTemplate.getRestTemplate().getMessageConverters().stream().filter(f -> f instanceof MappingJackson2HttpMessageConverter).map(f -> (MappingJackson2HttpMessageConverter) f).map(f -> f.getObjectMapper()).findFirst().orElseThrow(() -> new RuntimeException("no object mapper"));
        String x = objectMapper.writeValueAsString(request);
        System.out.println(x);

        ResponseEntity<ScheduleTimeslot> response =
                this.restTemplate.postForEntity(
                        "/ScheduleTimeslot/createScheduleTimeslot", request, ScheduleTimeslot.class);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
        testScheduleTimeslot = response.getBody();
        assertScheduleTimeslot(request, testScheduleTimeslot);
    }

    @Test
    @Order(2)
    public void testListAllScheduleTimeslots() {
        ScheduleTimeslotFilter request = new ScheduleTimeslotFilter();
        ParameterizedTypeReference<PaginationResponse<ScheduleTimeslot>> t =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<PaginationResponse<ScheduleTimeslot>> response =
                this.restTemplate.exchange(
                        "/ScheduleTimeslot/getAllScheduleTimeslots",
                        HttpMethod.POST,
                        new HttpEntity<>(request),
                        t);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
        PaginationResponse<ScheduleTimeslot> body = response.getBody();
        Assertions.assertNotNull(body);
        List<ScheduleTimeslot> ScheduleTimeslots = body.getList();
        Assertions.assertNotEquals(0, ScheduleTimeslots.size());
        Assertions.assertTrue(
                ScheduleTimeslots.stream().anyMatch(f -> f.getId().equals(testScheduleTimeslot.getId())));
    }

    public void assertScheduleTimeslot(
            ScheduleTimeslotCreate request, ScheduleTimeslot testScheduleTimeslot) {
        Assertions.assertNotNull(testScheduleTimeslot);

        if (request.getEndTimeOffsetId() != null) {

            Assertions.assertEquals(
                    request.getEndTimeOffsetId(), testScheduleTimeslot.getEndTimeOffsetId());
        }

        if (request.getStartTimeOfTheDayName() != null) {

            Assertions.assertEquals(
                    request.getStartTimeOfTheDayName(), testScheduleTimeslot.getStartTimeOfTheDayName());
        }

        if (request.getStartTime() != null) {

            Assertions.assertEquals(request.getStartTime(), testScheduleTimeslot.getStartTime().atZoneSameInstant(ZoneId.of(request.getStartTime().getOffset().getId())).toOffsetDateTime());
            Assertions.assertEquals(request.getStartTime().getOffset().getId(), testScheduleTimeslot.getStartTimeOffsetId());
        }

        if (request.getLastExecution() != null) {

            Assertions.assertEquals(request.getLastExecution(), testScheduleTimeslot.getLastExecution());
        }

        if (request.getStartMillisOffset() != null) {

            Assertions.assertEquals(
                    request.getStartMillisOffset(), testScheduleTimeslot.getStartMillisOffset());
        }

        if (request.getEndTimeOfTheDayName() != null) {

            Assertions.assertEquals(
                    request.getEndTimeOfTheDayName(), testScheduleTimeslot.getEndTimeOfTheDayName());
        }

        if (request.getScheduleId() != null) {

            Assertions.assertNotNull(testScheduleTimeslot.getSchedule());
            Assertions.assertEquals(request.getScheduleId(), testScheduleTimeslot.getSchedule().getId());
        }

        if (request.getTimeOfTheDayNameEndLon() != null) {

            Assertions.assertEquals(
                    request.getTimeOfTheDayNameEndLon(), testScheduleTimeslot.getTimeOfTheDayNameEndLon());
        }

        if (request.getTimeOfTheDayNameEndLat() != null) {

            Assertions.assertEquals(
                    request.getTimeOfTheDayNameEndLat(), testScheduleTimeslot.getTimeOfTheDayNameEndLat());
        }

        if (request.getEndTime() != null) {

            Assertions.assertEquals(request.getEndTime(), testScheduleTimeslot.getEndTime().atZoneSameInstant(ZoneId.of(request.getEndTime().getOffset().getId())).toOffsetDateTime());
            Assertions.assertEquals(request.getEndTime().getOffset().getId(), testScheduleTimeslot.getEndTimeOffsetId());

        }

        if (request.getCoolDownIntervalBeforeRepeat() != null) {

            Assertions.assertEquals(
                    request.getCoolDownIntervalBeforeRepeat(),
                    testScheduleTimeslot.getCoolDownIntervalBeforeRepeat());
        }

        if (request.getStartTimeOffsetId() != null) {

            Assertions.assertEquals(
                    request.getStartTimeOffsetId(), testScheduleTimeslot.getStartTimeOffsetId());
        }

        if (request.getEndMillisOffset() != null) {

            Assertions.assertEquals(
                    request.getEndMillisOffset(), testScheduleTimeslot.getEndMillisOffset());
        }

        if (request.getTimeOfTheDayNameStartLat() != null) {

            Assertions.assertEquals(
                    request.getTimeOfTheDayNameStartLat(),
                    testScheduleTimeslot.getTimeOfTheDayNameStartLat());
        }

        if (request.getTimeOfTheDayNameStartLon() != null) {

            Assertions.assertEquals(
                    request.getTimeOfTheDayNameStartLon(),
                    testScheduleTimeslot.getTimeOfTheDayNameStartLon());
        }
    }

    @Test
    @Order(3)
    public void testScheduleTimeslotUpdate() {
        String name = UUID.randomUUID().toString();
        ScheduleTimeslotUpdate request =
                new ScheduleTimeslotUpdate().setId(testScheduleTimeslot.getId()).setName(name);
        ResponseEntity<ScheduleTimeslot> response =
                this.restTemplate.exchange(
                        "/ScheduleTimeslot/updateScheduleTimeslot",
                        HttpMethod.PUT,
                        new HttpEntity<>(request),
                        ScheduleTimeslot.class);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
        testScheduleTimeslot = response.getBody();
        assertScheduleTimeslot(request, testScheduleTimeslot);
    }
}
