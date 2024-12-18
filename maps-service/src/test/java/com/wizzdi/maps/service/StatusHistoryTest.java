package com.wizzdi.maps.service;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.service.request.*;
import com.wizzdi.maps.service.response.StatusHistoryGroupedEntry;
import com.wizzdi.maps.service.response.StatusHistoryGroupedResponse;
import com.wizzdi.maps.service.service.MappedPOIService;
import com.wizzdi.maps.service.service.StatusHistoryGroupedService;
import com.wizzdi.maps.service.service.StatusHistoryService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
public class StatusHistoryTest {
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

    @Autowired
    private MappedPOIService mappedPOIService;
    @Autowired
    @Qualifier("historyIcons")
    private Map<String, MapIcon> historyIcons;
    @Autowired
    private SecurityContext securityContext;
    @Autowired
    private StatusHistoryService statusHistoryService;
    @Autowired
    private StatusHistoryGroupedService statusHistoryGroupedService;
    private List<MappedPOI> mappedPOIS;


    @BeforeAll
    public void initData() throws InterruptedException {
        MapIcon unknown= historyIcons.get(AppConfig.UNKNOWN);
        mappedPOIS=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MappedPOI mappedPOI = mappedPOIService.createMappedPOI(new MappedPOICreate().setKeepStatusHistory(true).setMapIcon(unknown).setName(i + ""), securityContext);
            mappedPOIS.add(mappedPOI);
        }
        waitForEventsToBeCreated(unknown);



    }

    @Test
    @Order(1)
    public void checkInitialEvents() throws InterruptedException {
        MapIcon unknown= historyIcons.get(AppConfig.UNKNOWN);

        Map<String,List<StatusHistory>> statusHistories = statusHistoryService.listAllStatusHistories(new StatusHistoryFilter().setMappedPOI(mappedPOIS), securityContext).stream().collect(Collectors.groupingBy(f->f.getMappedPOI().getId()));
        Assertions.assertFalse(statusHistories.isEmpty());
        for (MappedPOI pois : mappedPOIS) {
            List<StatusHistory> historyForMappedPOI = statusHistories.get(pois.getId());
            Assertions.assertNotNull(historyForMappedPOI);
            Assertions.assertTrue(historyForMappedPOI.stream().anyMatch(f->f.getMapIcon().getId().equals(unknown.getId())));
        }
    }

    @Test
    @Order(2)
    public void changeStatusToOn() throws InterruptedException {
        Thread.sleep(500);
        MapIcon on= historyIcons.get(AppConfig.ON);

        for (MappedPOI pois : mappedPOIS) {
            mappedPOIService.updateMappedPOI(new MappedPOIUpdate().setMappedPOI(pois).setMapIcon(on),securityContext);
        }
        Thread.sleep(1000);


    }

    @Test
    @Order(3)
    public void checkOnEvents() throws InterruptedException {

        MapIcon on= historyIcons.get(AppConfig.ON);

        Map<String,List<StatusHistory>> statusHistories = statusHistoryService.listAllStatusHistories(new StatusHistoryFilter().setMappedPOI(mappedPOIS), securityContext).stream().collect(Collectors.groupingBy(f->f.getMappedPOI().getId()));
        Assertions.assertFalse(statusHistories.isEmpty());
        for (MappedPOI pois : mappedPOIS) {
            List<StatusHistory> historyForMappedPOI = statusHistories.get(pois.getId());
            Assertions.assertNotNull(historyForMappedPOI);
            Assertions.assertTrue(historyForMappedPOI.stream().anyMatch(f->f.getMapIcon().getId().equals(on.getId())));
        }
    }


    @Test
    @Order(4)
    public void changeStatusToOff() throws InterruptedException {
        Thread.sleep(500);
        MapIcon off= historyIcons.get(AppConfig.OFF);

        for (MappedPOI pois : mappedPOIS) {
            mappedPOIService.updateMappedPOI(new MappedPOIUpdate().setMappedPOI(pois).setMapIcon(off),securityContext);
        }
        Thread.sleep(1000);


    }

    @Test
    @Order(5)
    public void checkOffEvents() throws InterruptedException {

        MapIcon off= historyIcons.get(AppConfig.OFF);

        Map<String,List<StatusHistory>> statusHistories = statusHistoryService.listAllStatusHistories(new StatusHistoryFilter().setMappedPOI(mappedPOIS), securityContext).stream().collect(Collectors.groupingBy(f->f.getMappedPOI().getId()));
        Assertions.assertFalse(statusHistories.isEmpty());
        for (MappedPOI pois : mappedPOIS) {
            List<StatusHistory> historyForMappedPOI = statusHistories.get(pois.getId());
            Assertions.assertNotNull(historyForMappedPOI);
            Assertions.assertTrue(historyForMappedPOI.stream().anyMatch(f->f.getMapIcon().getId().equals(off.getId())));
        }
    }

    @Test
    @Order(6)
    public void testStatusGrouped() throws InterruptedException {
        Map<String, OffsetDateTime> lastTimeForStatus = statusHistoryService.listAllStatusHistories(new StatusHistoryFilter().setMappedPOI(mappedPOIS), securityContext).stream().collect(Collectors.toMap(f->f.getMapIcon().getName(), f->f.getDateAtStatus(),(a,b)->a.isAfter(b)?a:b));
        Set<String> ids = mappedPOIS.stream().map(f -> f.getId()).collect(Collectors.toSet());

        for (Map.Entry<String, OffsetDateTime> stringOffsetDateTimeEntry : lastTimeForStatus.entrySet()) {
            MapIcon mapIcon = historyIcons.get(stringOffsetDateTimeEntry.getKey());
            OffsetDateTime value = stringOffsetDateTimeEntry.getValue().plus(100, ChronoUnit.MILLIS);
            StatusHistoryGroupedResponse statusHistoryGroupedResponse = statusHistoryGroupedService.listAllStatusHistoriesGrouped(new StatusHistoryGroupedRequest().setStatusAtDate(value).setMappedPOIFilter(new MappedPOIFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setOnlyIds(ids))), securityContext);
            Assertions.assertEquals(1,statusHistoryGroupedResponse.getStatusHistoryGroupedEntries().size(),"had more then one status , result: "+statusHistoryGroupedResponse.getStatusHistoryGroupedEntries().stream().map(f->f.getMapIcon().getName()+":"+f.getCount()).collect(Collectors.joining(System.lineSeparator())));
            StatusHistoryGroupedEntry statusHistoryGroupedEntry = statusHistoryGroupedResponse.getStatusHistoryGroupedEntries().get(0);
            Assertions.assertEquals(10, statusHistoryGroupedEntry.getCount());
            Assertions.assertEquals(mapIcon.getId(), statusHistoryGroupedEntry.getMapIcon().getId());

        }

    }

    private void waitForEventsToBeCreated(MapIcon mapIcon) throws InterruptedException {
        List<StatusHistory> history;
        while((history=statusHistoryService.listAllStatusHistories(new StatusHistoryFilter().setMappedPOI(mappedPOIS).setMapIcon(Collections.singletonList(mapIcon)), securityContext)).size()<mappedPOIS.size()){
            Thread.sleep(300);
        }
    }


}
