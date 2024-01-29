package com.flexicore.ui.tree;


import com.flexicore.ui.tree.app.App;
import com.flexicore.ui.tree.model.TreeNode;
import com.flexicore.ui.tree.request.*;
import com.flexicore.ui.tree.response.SaveTreeNodeStatusResponse;
import com.flexicore.ui.tree.response.TreeNodeStatusResponse;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.Map;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour

public class TreeToUserControllerTest {

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
    private TestRestTemplate restTemplate;
    @Autowired
    private TreeNode treeNode;


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
    public void testSaveTreeNodeStatus() {
        SaveTreeNodeStatusRequest request = new SaveTreeNodeStatusRequest()
                .setNodeStatus(Map.of(treeNode.getId(),true) );
        ResponseEntity<SaveTreeNodeStatusResponse> treeNodeResponse = this.restTemplate.postForEntity("/plugins/treeToUser/saveTreeNodeStatus", request, SaveTreeNodeStatusResponse.class);
        Assertions.assertEquals(200, treeNodeResponse.getStatusCodeValue());
        SaveTreeNodeStatusResponse saveTreeNodeStatusResponse = treeNodeResponse.getBody();
        Assertions.assertNotNull(saveTreeNodeStatusResponse);
        Assertions.assertEquals(1,saveTreeNodeStatusResponse.getCreated());

    }

    @Test
    @Order(2)
    public void testGetTreeNodeStatus() {
        TreeNodeStatusRequest request = new TreeNodeStatusRequest()
                .setNodeIds(Collections.singleton(treeNode.getId()));
        ResponseEntity<String> treeNodeResponse = this.restTemplate.postForEntity("/plugins/treeToUser/getTreeNodeStatus", request, String.class);
        Assertions.assertEquals(200, treeNodeResponse.getStatusCodeValue());
        String treeNodeResponseBody = treeNodeResponse.getBody();
        Assertions.assertNotNull(treeNodeResponseBody);
      /*  Assertions.assertNotNull(treeNodeResponseBody.getStatus());
        Boolean open = treeNodeResponseBody.getStatus().get(treeNode.getId());
        Assertions.assertNotNull(open);
        Assertions.assertTrue(open);*/

    }

}
