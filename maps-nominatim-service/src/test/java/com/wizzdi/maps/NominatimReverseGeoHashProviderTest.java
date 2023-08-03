package com.wizzdi.maps;


import com.wizzdi.maps.service.interfaces.AddressInfo;
import com.wizzdi.maps.service.reverse.geocode.NominatimReverseGeoHashProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour

public class NominatimReverseGeoHashProviderTest {

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
    private static final Logger logger = LoggerFactory.getLogger(NominatimReverseGeoHashProviderTest.class);
    @Autowired
    private NominatimReverseGeoHashProvider nominatimReverseGeoHashProvider;

    @Test
    @Order(1)
    public void testGetAddress() throws InterruptedException {
        AddressInfo address = nominatimReverseGeoHashProvider.getAddress(32.06243623542148, 34.79);
        Assertions.assertNotNull(address);
        Assertions.assertNotNull(address.cityInfo());
        Assertions.assertNotNull(address.countryInfo());
        Assertions.assertNotNull(address.road());
        Assertions.assertNotNull(address.uniqueId());


    }

}
