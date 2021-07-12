package com.wizzdi.flexicore.billing;

import com.wizzdi.flexicore.billing.app.App;
import com.wizzdi.flexicore.contract.model.Contract;
import com.wizzdi.flexicore.billing.request.ContractCreate;
import com.wizzdi.flexicore.billing.request.ContractFiltering;
import com.wizzdi.flexicore.billing.request.ContractUpdate;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class ContractControllerTest {

    private Contract contract;
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
    public void testContractCreate() {
        String name = UUID.randomUUID().toString();
        ContractCreate request = new ContractCreate()
                .setName(name);
        ResponseEntity<Contract> contractResponse = this.restTemplate.postForEntity("/plugins/Contract/createContract", request, Contract.class);
        Assertions.assertEquals(200, contractResponse.getStatusCodeValue());
        contract = contractResponse.getBody();
        assertContract(request, contract);

    }

    @Test
    @Order(2)
    public void testListAllContracts() {
        ContractFiltering request=new ContractFiltering();
        ParameterizedTypeReference<PaginationResponse<Contract>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Contract>> contractResponse = this.restTemplate.exchange("/plugins/Contract/getAllContracts", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, contractResponse.getStatusCodeValue());
        PaginationResponse<Contract> body = contractResponse.getBody();
        Assertions.assertNotNull(body);
        List<Contract> contracts = body.getList();
        Assertions.assertNotEquals(0,contracts.size());
        Assertions.assertTrue(contracts.stream().anyMatch(f->f.getId().equals(contract.getId())));


    }

    public void assertContract(ContractCreate request, Contract contract) {
        Assertions.assertNotNull(contract);
        Assertions.assertEquals(request.getName(), contract.getName());
    }

    @Test
    @Order(3)
    public void testContractUpdate(){
        String name = UUID.randomUUID().toString();
        ContractUpdate request = new ContractUpdate()
                .setId(contract.getId())
                .setName(name);
        ResponseEntity<Contract> contractResponse = this.restTemplate.exchange("/plugins/Contract/updateContract",HttpMethod.PUT, new HttpEntity<>(request), Contract.class);
        Assertions.assertEquals(200, contractResponse.getStatusCodeValue());
        contract = contractResponse.getBody();
        assertContract(request, contract);

    }

}
