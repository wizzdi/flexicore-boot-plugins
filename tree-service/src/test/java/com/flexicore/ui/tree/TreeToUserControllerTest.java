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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class TreeToUserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TreeNode treeNode;


    @BeforeAll
    private void init() {
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
