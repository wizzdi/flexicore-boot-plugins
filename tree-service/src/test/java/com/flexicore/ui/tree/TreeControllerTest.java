package com.flexicore.ui.tree;


import com.flexicore.ui.tree.app.App;
import com.flexicore.ui.tree.model.Tree;
import com.flexicore.ui.tree.model.TreeNode;
import com.flexicore.ui.tree.request.TreeCreate;
import com.flexicore.ui.tree.request.TreeFilter;
import com.flexicore.ui.tree.request.TreeUpdate;
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
import java.util.Map;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour

public class TreeControllerTest {

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
    private Tree tree;
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
    public void testTreeCreate() {
        String name = UUID.randomUUID().toString();
        TreeCreate request = new TreeCreate()
                .setRootId(treeNode.getId())
                .setJsonNode(Map.of("test","test"))
                .setName(name);
        ResponseEntity<Tree> treeResponse = this.restTemplate.postForEntity("/plugins/tree/createTree", request, Tree.class);
        Assertions.assertEquals(200, treeResponse.getStatusCodeValue());
        tree = treeResponse.getBody();
        assertTree(request, tree);

    }

    @Test
    @Order(2)
    public void testGetAllTrees() {
        TreeFilter request=new TreeFilter();
        ParameterizedTypeReference<PaginationResponse<Tree>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Tree>> treeResponse = this.restTemplate.exchange("/plugins/tree/getAllTrees", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, treeResponse.getStatusCodeValue());
        PaginationResponse<Tree> body = treeResponse.getBody();
        Assertions.assertNotNull(body);
        List<Tree> trees = body.getList();
        Assertions.assertNotEquals(0,trees.size());
        Assertions.assertTrue(trees.stream().anyMatch(f->f.getId().equals(tree.getId())));


    }

    public void assertTree(TreeCreate request, Tree tree) {
        Assertions.assertNotNull(tree);
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), tree.getName());
        }

        if(request.getRootId()!=null){
            Assertions.assertEquals(request.getRootId(),tree.getRoot().getId());
        }
        if(request.any()!=null){
            Assertions.assertNotNull(tree.any());
            for (Map.Entry<String, Object> stringObjectEntry : request.any().entrySet()) {
                Object val = tree.any().get(stringObjectEntry.getKey());
                Assertions.assertNotNull(val);
                Assertions.assertEquals(stringObjectEntry.getValue(),val);
            }
        }
    }

    @Test
    @Order(3)
    public void testTreeUpdate(){
        String name = UUID.randomUUID().toString();
        TreeUpdate request = new TreeUpdate()
                .setTreeId(tree.getId())
                .setName(name);
        ResponseEntity<Tree> treeResponse = this.restTemplate.exchange("/plugins/tree/updateTree",HttpMethod.PUT, new HttpEntity<>(request), Tree.class);
        Assertions.assertEquals(200, treeResponse.getStatusCodeValue());
        tree = treeResponse.getBody();
        assertTree(request, tree);

    }

}
