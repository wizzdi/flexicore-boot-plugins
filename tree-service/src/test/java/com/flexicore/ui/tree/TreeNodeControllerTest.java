package com.flexicore.ui.tree;


import com.flexicore.ui.tree.app.App;
import com.flexicore.ui.tree.model.TreeNode;
import com.flexicore.ui.tree.request.TreeNodeCreate;
import com.flexicore.ui.tree.request.TreeNodeFilter;
import com.flexicore.ui.tree.request.TreeNodeUpdate;
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

public class TreeNodeControllerTest {

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
    private TreeNode treeNode;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    @Qualifier("treeNode")
    private TreeNode parentTreeNode;
    @Autowired
    private DynamicExecution dynamicExecution;

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
    public void testTreeNodeCreate() {
        String name = UUID.randomUUID().toString();
        TreeNodeCreate request = new TreeNodeCreate()
                .setDynamicExecutionId(dynamicExecution.getId())
                .setParentId(parentTreeNode.getId())
                .setJsonNode(Map.of("test","test"))
                .setName(name);
        ResponseEntity<TreeNode> treeNodeResponse = this.restTemplate.postForEntity("/plugins/treeNode/createTreeNode", request, TreeNode.class);
        Assertions.assertEquals(200, treeNodeResponse.getStatusCodeValue());
        treeNode = treeNodeResponse.getBody();
        assertTreeNode(request, treeNode);

    }

    @Test
    @Order(2)
    public void testGetAllTreeNodes() {
        TreeNodeFilter request=new TreeNodeFilter();
        ParameterizedTypeReference<PaginationResponse<TreeNode>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<TreeNode>> treeNodeResponse = this.restTemplate.exchange("/plugins/treeNode/getAllTreeNodes", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, treeNodeResponse.getStatusCodeValue());
        PaginationResponse<TreeNode> body = treeNodeResponse.getBody();
        Assertions.assertNotNull(body);
        List<TreeNode> treeNodes = body.getList();
        Assertions.assertNotEquals(0,treeNodes.size());
        Assertions.assertTrue(treeNodes.stream().anyMatch(f->f.getId().equals(treeNode.getId())));


    }

    public void assertTreeNode(TreeNodeCreate request, TreeNode treeNode) {
        Assertions.assertNotNull(treeNode);
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), treeNode.getName());
        }

        if(request.getParentId()!=null){
            Assertions.assertEquals(request.getParentId(),treeNode.getParent().getId());
        }
        if(request.getDynamicExecutionId()!=null){
            Assertions.assertEquals(request.getDynamicExecutionId(),treeNode.getDynamicExecution().getId());
        }
        if(request.any()!=null){
            Assertions.assertNotNull(treeNode.any());
            for (Map.Entry<String, Object> stringObjectEntry : request.any().entrySet()) {
                Object val = treeNode.any().get(stringObjectEntry.getKey());
                Assertions.assertNotNull(val);
                Assertions.assertEquals(stringObjectEntry.getValue(),val);
            }
        }
    }

    @Test
    @Order(3)
    public void testTreeNodeUpdate(){
        String name = UUID.randomUUID().toString();
        TreeNodeUpdate request = new TreeNodeUpdate()
                .setNodeId(treeNode.getId())
                .setName(name);
        ResponseEntity<TreeNode> treeNodeResponse = this.restTemplate.exchange("/plugins/treeNode/updateTreeNode",HttpMethod.PUT, new HttpEntity<>(request), TreeNode.class);
        Assertions.assertEquals(200, treeNodeResponse.getStatusCodeValue());
        treeNode = treeNodeResponse.getBody();
        assertTreeNode(request, treeNode);

    }

}
