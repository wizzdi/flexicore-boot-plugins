package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.service.data.RemoteRepository;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Extension
public class RemoteIndexCreator implements Plugin, InitializingBean {
    @Autowired
    private RemoteRepository remoteRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        remoteRepository.createRemoteIdx();

    }
}
