package com.wizzdi.flexicore.encryption;

import com.wizzdi.flexicore.encryption.app.App;
import com.wizzdi.flexicore.encryption.service.CommonEncryptionService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.GeneralSecurityException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class CommonEncryptionServiceTest {


    @Autowired
    private CommonEncryptionService commonEncryptionService;


    @Test
    @Order(0)
    public void testEncrypt() throws GeneralSecurityException {
        String plainText="test";
        byte[] associatedData = "key".getBytes();
        byte[] encrypted = commonEncryptionService.encrypt(plainText.getBytes(), associatedData);
        byte[] decrypted = commonEncryptionService.decrypt(encrypted, associatedData);
        String output=new String(decrypted);
        Assertions.assertEquals(plainText,output);

    }

}
