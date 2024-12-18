package com.wizzdi.maps.service;

import com.flexicore.model.territories.Address;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.service.request.*;
import com.wizzdi.maps.service.response.MapFilterComponent;
import com.wizzdi.maps.service.response.StatusHistoryGroupedEntry;
import com.wizzdi.maps.service.response.StatusHistoryGroupedResponse;
import com.wizzdi.maps.service.service.MapFilterComponentService;
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
public class MapFilterComponentTest {

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
    private MapFilterComponentService mapFilterComponentService;
    @Autowired
    private Address newyorkAddress;
    @Autowired
    private Address telAvivAddress;

    private List<MappedPOI> mappedPOIS;

    @BeforeAll
    public void initData() throws InterruptedException {

        mappedPOIS=new ArrayList<>();
        for (MapIcon value : historyIcons.values()) {
            for (int i = 0; i < 4; i++) {
                Address address=i%2==0?newyorkAddress:telAvivAddress;
                MappedPOI mappedPOI = mappedPOIService.createMappedPOI(new MappedPOICreate().setAddress(address).setMapIcon(value).setName("test "+value.getName()+" "+i + ""), securityContext);
                mappedPOIS.add(mappedPOI);
            }
        }

        for (MapIcon value : historyIcons.values()) {
            for (int i = 0; i < 4; i++) {
                Address address=i%2==0?newyorkAddress:telAvivAddress;
                MappedPOI mappedPOI = mappedPOIService.createMappedPOI(new MappedPOICreate().setAddress(address).setMapIcon(value).setName("other "+value.getName()+" "+i + ""), securityContext);
            }
        }




    }

    @Test
    @Order(1)
    public void testFilterComponentByMapIcon(){
        MapFilterComponentRequest mapFilterComponentRequest = new MapFilterComponentRequest()
                .setFilterComponentPropertyProvider(new StandardFilterComponentProvider())
                .setMappedPOIFilter(new MappedPOIFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike("%test%")))
                .setFilterComponentType(FilterComponentType.MAP_ICON);
        PaginationResponse<MapFilterComponent> allMapFilterComponents = mapFilterComponentService.getAllMapFilterComponents(mapFilterComponentRequest, securityContext);
        List<MapFilterComponent> list = allMapFilterComponents.getList();
        Assertions.assertEquals(3, list.size());
        for (MapFilterComponent mapFilterComponent : list) {
                Assertions.assertEquals(4,mapFilterComponent.getCount());
        }

    }

    @Test
    @Order(2)
    public void testFilterComponentWithNameLike(){
        MapFilterComponentRequest mapFilterComponentRequest = new MapFilterComponentRequest()
                .setFilterComponentPropertyProvider(new StandardFilterComponentProvider())
                .setMappedPOIFilter(new MappedPOIFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike("%test%")))
                .setFilterComponentType(FilterComponentType.MAP_ICON)
                .setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike("%on%"));
        PaginationResponse<MapFilterComponent> allMapFilterComponents = mapFilterComponentService.getAllMapFilterComponents(mapFilterComponentRequest, securityContext);
        List<MapFilterComponent> list = allMapFilterComponents.getList();

        Assertions.assertEquals(1, list.size());
        for (MapFilterComponent mapFilterComponent : list) {
            Assertions.assertEquals(4,mapFilterComponent.getCount());
        }

    }

    @Test
    @Order(3)
    public void testFilterComponentWithPagination(){
        MapFilterComponentRequest mapFilterComponentRequest = new MapFilterComponentRequest()
                .setFilterComponentPropertyProvider(new StandardFilterComponentProvider())
                .setMappedPOIFilter(new MappedPOIFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike("%test%")))
                .setFilterComponentType(FilterComponentType.MAP_ICON)
                .setPageSize(2)
                .setCurrentPage(0);
        PaginationResponse<MapFilterComponent> allMapFilterComponents = mapFilterComponentService.getAllMapFilterComponents(mapFilterComponentRequest, securityContext);
        List<MapFilterComponent> list = allMapFilterComponents.getList();

        Assertions.assertEquals(2, list.size());
        for (MapFilterComponent mapFilterComponent : list) {
            Assertions.assertEquals(4,mapFilterComponent.getCount());
        }
        Set<String> ids=list.stream().map(f->f.getId()).collect(Collectors.toSet());
        mapFilterComponentRequest.setCurrentPage(1);
        allMapFilterComponents = mapFilterComponentService.getAllMapFilterComponents(mapFilterComponentRequest, securityContext);
        list = allMapFilterComponents.getList();
        Assertions.assertEquals(1, list.size());
        Assertions.assertTrue(list.stream().noneMatch(ids::contains));
        for (MapFilterComponent mapFilterComponent : list) {
            Assertions.assertEquals(4,mapFilterComponent.getCount());
        }



    }

    @Test
    @Order(4)
    public void testFilterComponentByCountry(){
        MapFilterComponentRequest mapFilterComponentRequest = new MapFilterComponentRequest()
                .setFilterComponentPropertyProvider(new StandardFilterComponentProvider())
                .setMappedPOIFilter(new MappedPOIFilter().setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike("%test%")))
                .setFilterComponentType(FilterComponentType.COUNTRY);
        PaginationResponse<MapFilterComponent> allMapFilterComponents = mapFilterComponentService.getAllMapFilterComponents(mapFilterComponentRequest, securityContext);
        List<MapFilterComponent> list = allMapFilterComponents.getList();
        Assertions.assertEquals(2, list.size());
        for (MapFilterComponent mapFilterComponent : list) {
            Assertions.assertEquals(mappedPOIS.size()/2,mapFilterComponent.getCount());
        }

    }

}
