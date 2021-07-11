package com.wizzdi.flexicore.pricing;

import com.wizzdi.flexicore.pricing.app.App;
import com.wizzdi.flexicore.pricing.model.price.RecurringPriceEntry;
import com.wizzdi.flexicore.pricing.request.RecurringPriceEntryCreate;
import com.wizzdi.flexicore.pricing.request.RecurringPriceEntryFiltering;
import com.wizzdi.flexicore.pricing.request.RecurringPriceEntryUpdate;
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

public class RecurringPriceEntryControllerTest {

    private RecurringPriceEntry recurringPriceEntry;
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
    public void testRecurringPriceEntryCreate() {
        String name = UUID.randomUUID().toString();
        RecurringPriceEntryCreate request = new RecurringPriceEntryCreate()
                .setName(name);
        ResponseEntity<RecurringPriceEntry> recurringPriceEntryResponse = this.restTemplate.postForEntity("/plugins/RecurringPriceEntry/createRecurringPriceEntry", request, RecurringPriceEntry.class);
        Assertions.assertEquals(200, recurringPriceEntryResponse.getStatusCodeValue());
        recurringPriceEntry = recurringPriceEntryResponse.getBody();
        assertRecurringPriceEntry(request, recurringPriceEntry);

    }

    @Test
    @Order(2)
    public void testListAllRecurringPriceEntrys() {
        RecurringPriceEntryFiltering request=new RecurringPriceEntryFiltering();
        ParameterizedTypeReference<PaginationResponse<RecurringPriceEntry>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<RecurringPriceEntry>> recurringPriceEntryResponse = this.restTemplate.exchange("/plugins/RecurringPriceEntry/getAllCurrencies", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, recurringPriceEntryResponse.getStatusCodeValue());
        PaginationResponse<RecurringPriceEntry> body = recurringPriceEntryResponse.getBody();
        Assertions.assertNotNull(body);
        List<RecurringPriceEntry> recurringPriceEntrys = body.getList();
        Assertions.assertNotEquals(0,recurringPriceEntrys.size());
        Assertions.assertTrue(recurringPriceEntrys.stream().anyMatch(f->f.getId().equals(recurringPriceEntry.getId())));


    }

    public void assertRecurringPriceEntry(RecurringPriceEntryCreate request, RecurringPriceEntry recurringPriceEntry) {
        Assertions.assertNotNull(recurringPriceEntry);
        Assertions.assertEquals(request.getName(), recurringPriceEntry.getName());
    }

    @Test
    @Order(3)
    public void testRecurringPriceEntryUpdate(){
        String name = UUID.randomUUID().toString();
        RecurringPriceEntryUpdate request = new RecurringPriceEntryUpdate()
                .setId(recurringPriceEntry.getId())
                .setName(name);
        ResponseEntity<RecurringPriceEntry> recurringPriceEntryResponse = this.restTemplate.exchange("/plugins/RecurringPriceEntry/updateRecurringPriceEntry",HttpMethod.PUT, new HttpEntity<>(request), RecurringPriceEntry.class);
        Assertions.assertEquals(200, recurringPriceEntryResponse.getStatusCodeValue());
        recurringPriceEntry = recurringPriceEntryResponse.getBody();
        assertRecurringPriceEntry(request, recurringPriceEntry);

    }

}
