package com.wizzdi.basic.iot.service;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.request.FileResourceCreate;
import com.wizzdi.flexicore.file.service.FileResourceService;
import com.wizzdi.flexicore.file.service.MD5Service;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Configuration
public class FirmwareProvider {

    @Bean
    @Lazy
    public ObjectHolder<FileResource> firmwareFile(MD5Service md5Service,FileResourceService fileResourceService, SecurityContext adminSecurityContext) throws IOException {
        File file=new File(FileUtils.getTempDirectory(), UUID.randomUUID()+"firmware.bin");
        FileUtils.writeStringToFile(file,"this is a test firmware", StandardCharsets.UTF_8);
        FileResource fileResource = fileResourceService.createFileResource(new FileResourceCreate().setMd5(md5Service.generateMD5(file)).setFullPath(file.getAbsolutePath()).setName(file.getName()), adminSecurityContext);
        return new ObjectHolder<>(fileResource);
    }
}
