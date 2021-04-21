package com.wizzdi.user.profile.app;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.request.FileResourceCreate;
import com.wizzdi.flexicore.file.service.FileResourceService;
import com.wizzdi.flexicore.file.service.MD5Service;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

@Configuration
public class TestEntities {

    @Autowired
    private FileResourceService fileResourceService;
    @Autowired
    private MD5Service md5Service;
    @Autowired
    @Lazy
    private SecurityContextBase adminSecurityContext;

    @Bean
    public FileResource avatar() throws IOException {

        String s = UUID.randomUUID().toString();
        File avatar = File.createTempFile("test", "png");
        FileUtils.write(avatar,s, StandardCharsets.UTF_8);
        String md5=md5Service.generateMD5(avatar);
        return fileResourceService.createFileResource(new FileResourceCreate().setMd5(md5).setActualFilename(avatar.getName()).setFullPath(avatar.getAbsolutePath()).setName("test"), adminSecurityContext);

    }
}
