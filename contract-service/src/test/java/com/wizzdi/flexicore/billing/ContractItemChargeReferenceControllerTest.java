package com.wizzdi.flexicore.billing;

import com.wizzdi.flexicore.billing.app.App;
import com.wizzdi.flexicore.billing.request.ContractItemChargeReferenceCreate;
import com.wizzdi.flexicore.billing.request.ContractItemChargeReferenceFiltering;
import com.wizzdi.flexicore.billing.request.ContractItemChargeReferenceUpdate;
import com.wizzdi.flexicore.contract.model.ContractItemChargeReference;
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

public class ContractItemChargeReferenceControllerTest {

    private ContractItemChargeReference contractItemChargeReference;
    @Autowired
    private TestRestTemplate restTemplate;

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
    public void testContractItemChargeReferenceCreate() {
        String name = UUID.randomUUID().toString();
        ContractItemChargeReferenceCreate request = new ContractItemChargeReferenceCreate()
                .setName(name);
        ResponseEntity<ContractItemChargeReference> contractItemChargeReferenceResponse = this.restTemplate.postForEntity("/plugins/ContractItemChargeReference/createContractItemChargeReference", request, ContractItemChargeReference.class);
        Assertions.assertEquals(200, contractItemChargeReferenceResponse.getStatusCodeValue());
        contractItemChargeReference = contractItemChargeReferenceResponse.getBody();
        assertContractItemChargeReference(request, contractItemChargeReference);

    }

    @Test
    @Order(2)
    public void testListAllContractItemChargeReferences() {
        ContractItemChargeReferenceFiltering request=new ContractItemChargeReferenceFiltering();
        ParameterizedTypeReference<PaginationResponse<ContractItemChargeReference>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<ContractItemChargeReference>> contractItemChargeReferenceResponse = this.restTemplate.exchange("/plugins/ContractItemChargeReference/getAllContractItemChargeReferences", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, contractItemChargeReferenceResponse.getStatusCodeValue());
        PaginationResponse<ContractItemChargeReference> body = contractItemChargeReferenceResponse.getBody();
        Assertions.assertNotNull(body);
        List<ContractItemChargeReference> contractItemChargeReferences = body.getList();
        Assertions.assertNotEquals(0,contractItemChargeReferences.size());
        Assertions.assertTrue(contractItemChargeReferences.stream().anyMatch(f->f.getId().equals(contractItemChargeReference.getId())));


    }

    public void assertContractItemChargeReference(ContractItemChargeReferenceCreate request, ContractItemChargeReference contractItemChargeReference) {
        Assertions.assertNotNull(contractItemChargeReference);
        Assertions.assertEquals(request.getName(), contractItemChargeReference.getName());
    }

    @Test
    @Order(3)
    public void testContractItemChargeReferenceUpdate(){
        String name = UUID.randomUUID().toString();
        ContractItemChargeReferenceUpdate request = new ContractItemChargeReferenceUpdate()
                .setId(contractItemChargeReference.getId())
                .setName(name);
        ResponseEntity<ContractItemChargeReference> contractItemChargeReferenceResponse = this.restTemplate.exchange("/plugins/ContractItemChargeReference/updateContractItemChargeReference",HttpMethod.PUT, new HttpEntity<>(request), ContractItemChargeReference.class);
        Assertions.assertEquals(200, contractItemChargeReferenceResponse.getStatusCodeValue());
        contractItemChargeReference = contractItemChargeReferenceResponse.getBody();
        assertContractItemChargeReference(request, contractItemChargeReference);

    }

}
