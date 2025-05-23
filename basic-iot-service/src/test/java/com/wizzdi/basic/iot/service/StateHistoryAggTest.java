package com.wizzdi.basic.iot.service;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.basic.iot.service.response.StateHistoryAggEntry;
import com.wizzdi.basic.iot.service.service.RemoteService;
import com.wizzdi.basic.iot.service.service.StateHistoryAggService;
import com.wizzdi.basic.iot.service.service.StateHistoryService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
@DirtiesContext
public class StateHistoryAggTest {

    private final static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:15")

            .withDatabaseName("flexicore-test")
            .withUsername("flexicore")
            .withPassword("flexicore");
    private final static HiveMQContainer hivemqCe = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce").withTag("2023.5"));


    static {
        postgresqlContainer.start();
        hivemqCe.start();

    }



    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);

        registry.add("basic.iot.mqtt.url", () -> "tcp://" + hivemqCe.getHost() + ":" + hivemqCe.getMqttPort());
        KeyPairForTests.KeyPair keyPair = KeyPairForTests.getKeyPair();
        File privateKey = keyPair.privateKey();
        registry.add("basic.iot.keyPath", privateKey::getAbsolutePath);

    }

    private Remote remote1;
    private Remote remote2;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private RemoteService remoteService;
    @Autowired
    private StateHistoryService stateHistoryService;
    @Autowired
    private StateHistoryAggService stateHistoryAggService;
    @Autowired
    @Lazy
    private SecurityContext adminSecurityContext;


    @BeforeAll
    public void init() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));

        remote1 = remoteService.createRemote(new RemoteCreate().setName("remote1"), adminSecurityContext);
        remote2 = remoteService.createRemote(new RemoteCreate().setName("remote2"), adminSecurityContext);
        OffsetDateTime start = OffsetDateTime.now();

        for (Remote remote : List.of(remote1, remote2)) {
            for (int i = 0; i < 10; i++) {
                StateHistoryCreate stateHistoryCreate = new StateHistoryCreate()
                        .setRemote(remote)
                        .setTimeAtState(start.minus(Duration.ofDays(i)))
                                .setDeviceProperties(Map.of("valueMap", Map.of("value", 10)));
                stateHistoryService.createStateHistory(stateHistoryCreate, adminSecurityContext);
            }
        }

    }



    @Test
    @Order(1)
    public void testAgg() {
        StateHistoryAggRequest stateHistoryAggRequest=new StateHistoryAggRequest()
                .setStateHistoryFilter(new StateHistoryFilter())
                        .setTimeUnit(StateHistoryAggTimeUnit.DAYS)
                                .setGroupByFieldName("valueMap.value");
        PaginationResponse<StateHistoryAggEntry> allStateHistoriesAgg = stateHistoryAggService.getAllStateHistoriesAgg(adminSecurityContext, stateHistoryAggRequest);
        Assertions.assertEquals(10,allStateHistoriesAgg.getList().size());
        List<StateHistoryAggEntry> list=allStateHistoriesAgg.getList();
        Assertions.assertTrue(list.stream().allMatch(f->f.getValue()==20));


    }

    @Test
    @Order(2)
    public void testAggRemote() {
        StateHistoryAggRequest stateHistoryAggRequest=new StateHistoryAggRequest()
                .setStateHistoryFilter(new StateHistoryFilter())
                .setTimeUnit(StateHistoryAggTimeUnit.DAYS)
                .setGroupByFieldName("valueMap.value")
                .setGroupByRemote(true);
        PaginationResponse<StateHistoryAggEntry> allStateHistoriesAgg = stateHistoryAggService.getAllStateHistoriesAgg(adminSecurityContext, stateHistoryAggRequest);
        Assertions.assertEquals(20,allStateHistoriesAgg.getList().size());
        List<StateHistoryAggEntry> list=allStateHistoriesAgg.getList();
        Assertions.assertTrue(list.stream().allMatch(f->f.getValue()==10));


    }



}
