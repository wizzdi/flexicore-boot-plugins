package com.flexicore.scheduling.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.App;
import com.flexicore.scheduling.request.ScheduleCreate;
import com.flexicore.scheduling.request.ScheduleFilter;
import com.flexicore.scheduling.request.ScheduleUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
public class ScheduleControllerTest {

  private Schedule testSchedule;
  @Autowired private TestRestTemplate restTemplate;

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
  public void testScheduleCreate() {
    String name = UUID.randomUUID().toString();
    ScheduleCreate request = new ScheduleCreate().setName(name);

    request.setEnabled(true);

    request.setMonday(true);

    request.setSaturday(true);

    request.setTimeFrameEnd(OffsetDateTime.now());

    request.setTuesday(true);

    request.setHoliday(true);

    request.setSunday(true);

    request.setThursday(true);

    request.setFriday(true);

    request.setTimeFrameStart(OffsetDateTime.now());

    request.setWednesday(true);

    ResponseEntity<Schedule> response =
        this.restTemplate.postForEntity("/Schedule/createSchedule", request, Schedule.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testSchedule = response.getBody();
    assertSchedule(request, testSchedule);
  }

  @Test
  @Order(2)
  public void testListAllSchedules() {
    ScheduleFilter request = new ScheduleFilter();
    ParameterizedTypeReference<PaginationResponse<Schedule>> t =
        new ParameterizedTypeReference<>() {};

    ResponseEntity<PaginationResponse<Schedule>> response =
        this.restTemplate.exchange(
            "/Schedule/getAllSchedules", HttpMethod.POST, new HttpEntity<>(request), t);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    PaginationResponse<Schedule> body = response.getBody();
    Assertions.assertNotNull(body);
    List<Schedule> Schedules = body.getList();
    Assertions.assertNotEquals(0, Schedules.size());
    Assertions.assertTrue(Schedules.stream().anyMatch(f -> f.getId().equals(testSchedule.getId())));
  }

  public void assertSchedule(ScheduleCreate request, Schedule testSchedule) {
    Assertions.assertNotNull(testSchedule);

    if (request.isEnabled() != null) {

      Assertions.assertEquals(request.isEnabled(), testSchedule.isEnabled());
    }

    if (request.isMonday() != null) {

      Assertions.assertEquals(request.isMonday(), testSchedule.isMonday());
    }

    if (request.isSaturday() != null) {

      Assertions.assertEquals(request.isSaturday(), testSchedule.isSaturday());
    }
/*
    if (request.getTimeFrameEnd() != null) {

      Assertions.assertEquals(request.getTimeFrameEnd(), testSchedule.getTimeFrameEnd().atZoneSameInstant(ZoneId.of(request.getTimeFrameEnd().getOffset().getId())).toOffsetDateTime());
    }
*/
    if (request.isTuesday() != null) {

      Assertions.assertEquals(request.isTuesday(), testSchedule.isTuesday());
    }

    if (request.isHoliday() != null) {

      Assertions.assertEquals(request.isHoliday(), testSchedule.isHoliday());
    }

    if (request.isSunday() != null) {

      Assertions.assertEquals(request.isSunday(), testSchedule.isSunday());
    }

    if (request.isThursday() != null) {

      Assertions.assertEquals(request.isThursday(), testSchedule.isThursday());
    }

    if (request.isFriday() != null) {

      Assertions.assertEquals(request.isFriday(), testSchedule.isFriday());
    }
/*
    if (request.getTimeFrameStart() != null) {

      Assertions.assertEquals(request.getTimeFrameStart(), testSchedule.getTimeFrameStart().atZoneSameInstant(ZoneId.of(request.getTimeFrameStart().getOffset().getId())).toOffsetDateTime());
    }
*/
    if (request.isWednesday() != null) {

      Assertions.assertEquals(request.isWednesday(), testSchedule.isWednesday());
    }
  }

  @Test
  @Order(3)
  public void testScheduleUpdate() {
    String name = UUID.randomUUID().toString();
    ScheduleUpdate request = new ScheduleUpdate().setId(testSchedule.getId()).setName(name);
    ResponseEntity<Schedule> response =
        this.restTemplate.exchange(
            "/Schedule/updateSchedule", HttpMethod.PUT, new HttpEntity<>(request), Schedule.class);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
    testSchedule = response.getBody();
    assertSchedule(request, testSchedule);
  }
}
