package com.wizzdi.flexicore.billing;

import com.wizzdi.flexicore.billing.app.App;
import com.wizzdi.flexicore.contract.model.ContractItem;
import com.wizzdi.flexicore.billing.request.ContractItemCreate;
import com.wizzdi.flexicore.billing.request.ContractItemFiltering;
import com.wizzdi.flexicore.billing.request.ContractItemUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
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

public class ContractItemControllerTest {

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
    private ContractItem contractItem;
    @Autowired
    private TestRestTemplate restTemplate;

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
    public void testContractItemCreate() {
        String name = UUID.randomUUID().toString();
        ContractItemCreate request = new ContractItemCreate()
                .setName(name);
        ResponseEntity<ContractItem> contractItemResponse = this.restTemplate.postForEntity("/plugins/ContractItem/createContractItem", request, ContractItem.class);
        Assertions.assertEquals(200, contractItemResponse.getStatusCodeValue());
        contractItem = contractItemResponse.getBody();
        assertContractItem(request, contractItem);

    }

    @Test
    @Order(2)
    public void testListAllContractItems() {
        ContractItemFiltering request=new ContractItemFiltering();
        ParameterizedTypeReference<PaginationResponse<ContractItem>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<ContractItem>> contractItemResponse = this.restTemplate.exchange("/plugins/ContractItem/getAllContractItems", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, contractItemResponse.getStatusCodeValue());
        PaginationResponse<ContractItem> body = contractItemResponse.getBody();
        Assertions.assertNotNull(body);
        List<ContractItem> contractItems = body.getList();
        Assertions.assertNotEquals(0,contractItems.size());
        Assertions.assertTrue(contractItems.stream().anyMatch(f->f.getId().equals(contractItem.getId())));


    }

    public void assertContractItem(ContractItemCreate request, ContractItem contractItem) {
        Assertions.assertNotNull(contractItem);
        Assertions.assertEquals(request.getName(), contractItem.getName());
    }

    @Test
    @Order(3)
    public void testContractItemUpdate(){
        String name = UUID.randomUUID().toString();
        ContractItemUpdate request = new ContractItemUpdate()
                .setId(contractItem.getId())
                .setName(name);
        ResponseEntity<ContractItem> contractItemResponse = this.restTemplate.exchange("/plugins/ContractItem/updateContractItem",HttpMethod.PUT, new HttpEntity<>(request), ContractItem.class);
        Assertions.assertEquals(200, contractItemResponse.getStatusCodeValue());
        contractItem = contractItemResponse.getBody();
        assertContractItem(request, contractItem);

    }

}
