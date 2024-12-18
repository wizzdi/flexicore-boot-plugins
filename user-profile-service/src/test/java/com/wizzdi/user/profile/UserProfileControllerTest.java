package com.wizzdi.user.profile;


import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.response.PaginationResponse;

import com.wizzdi.user.profile.app.App;
import com.wizzdi.user.profile.model.Gender;
import com.wizzdi.user.profile.model.UserProfile;
import com.wizzdi.user.profile.request.UserProfileCreate;
import com.wizzdi.user.profile.request.UserProfileFilter;
import com.wizzdi.user.profile.request.UserProfileUpdate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Lazy;
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
import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {App.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
public class UserProfileControllerTest {

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
    @Lazy
    @Qualifier("adminSecurityContext")
    private SecurityContext SecurityContext;

    @Autowired
    private FileResource avatar;



    private UserProfile userProfile;


    @BeforeAll
    public void init() {
        AtomicReference<String> reference=new AtomicReference<>("admin");
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", reference.get());
                    return execution.execute(request, body);
                }));

    }

    @Test
    @Order(1)
    public void createUserProfile() throws InterruptedException {
        UserProfileCreate request = new UserProfileCreate()
                .setAvatarId(avatar.getId())
                .setGender(Gender.MALE)
                .setUserId(SecurityContext.getUser().getId())
                .setName("test userProfile");

        ParameterizedTypeReference<UserProfile> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<UserProfile> response = this.restTemplate.exchange("/userProfile/createUserProfile", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
        userProfile = response.getBody();
        Assertions.assertNotNull(userProfile);
        assertProfile(request, userProfile);


    }

    private void assertProfile(UserProfileCreate request,UserProfile userProfile) {
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), userProfile.getName());

        }
        if(request.getAvatarId()!=null){
            Assertions.assertNotNull(userProfile.getAvatar());
            Assertions.assertEquals(request.getAvatarId(), userProfile.getAvatar().getId());
        }
        if(request.getUserId()!=null){
            Assertions.assertNotNull(userProfile.getSecurityUser());
            Assertions.assertEquals(request.getUserId(), userProfile.getSecurityUser().getId());
        }
        if(request.getGender()!=null){
            Assertions.assertEquals(request.getGender(), userProfile.getGender());
        }

    }

    @Test
    @Order(2)
    public void testGetAllUserProfiles() throws InterruptedException {
        UserProfileFilter request = new UserProfileFilter();
        ParameterizedTypeReference<PaginationResponse<UserProfile>> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<PaginationResponse<UserProfile>> response = this.restTemplate.exchange("/userProfile/getAllUserProfiles", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
        PaginationResponse<UserProfile> body = response.getBody();
        Assertions.assertNotNull(body);
        List<UserProfile> userProfiles = body.getList();
        Assertions.assertTrue(userProfiles.stream().anyMatch(f->f.getId().equals(userProfile.getId())));

    }

    @Test
    @Order(3)
    public void testUpdateUserProfile() throws InterruptedException {
        UserProfileUpdate request = new UserProfileUpdate()
                .setId(userProfile.getId())
                .setName("new name");
        ParameterizedTypeReference<UserProfile> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<UserProfile> response = this.restTemplate.exchange("/userProfile/updateUserProfile", HttpMethod.PUT, new HttpEntity<>(request), t);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
        userProfile= response.getBody();
        Assertions.assertNotNull(userProfile);
        assertProfile(request, userProfile);

    }


}
