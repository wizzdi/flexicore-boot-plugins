package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.FirmwareUpdate;
import com.wizzdi.basic.iot.service.data.FirmwareUpdateRepository;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateCreate;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateFilter;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.apache.commons.io.FileUtils;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@Extension
@Component

public class FirmwareUpdateService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(FirmwareUpdateService.class);

    @Autowired
    private FirmwareUpdateRepository repository;

    @Autowired
    private BasicService basicService;

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return repository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return repository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return repository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        repository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        repository.massMerge(toMerge);
    }

    public PaginationResponse<FirmwareUpdate> getAllFirmwareUpdates(
            SecurityContextBase securityContext, FirmwareUpdateFilter filtering) {
        List<FirmwareUpdate> list = listAllFirmwareUpdates(securityContext, filtering);
        long count = repository.countAllFirmwareUpdates(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<FirmwareUpdate> listAllFirmwareUpdates(SecurityContextBase securityContext, FirmwareUpdateFilter firmwareUpdateFilter) {
        return repository.getAllFirmwareUpdates(securityContext, firmwareUpdateFilter);
    }

    public FirmwareUpdate createFirmwareUpdate(FirmwareUpdateCreate creationContainer,
                               SecurityContextBase securityContext) {
        FirmwareUpdate firmwareUpdate = createFirmwareUpdateNoMerge(creationContainer, securityContext);
        repository.merge(firmwareUpdate);
        return firmwareUpdate;
    }

    public FirmwareUpdate createFirmwareUpdateNoMerge(FirmwareUpdateCreate creationContainer,
                                      SecurityContextBase securityContext) {
        FirmwareUpdate firmwareUpdate = new FirmwareUpdate();
        firmwareUpdate.setId(UUID.randomUUID().toString());

        updateFirmwareUpdateNoMerge(firmwareUpdate, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(firmwareUpdate, securityContext);
        return firmwareUpdate;
    }

    public boolean updateFirmwareUpdateNoMerge(FirmwareUpdate firmwareUpdate,
                                       FirmwareUpdateCreate firmwareUpdateCreate) {
        boolean update = basicService.updateBasicNoMerge(firmwareUpdateCreate, firmwareUpdate);
        if (firmwareUpdateCreate.getVersion() != null && !firmwareUpdateCreate.getVersion().equals(firmwareUpdate.getVersion())) {
            firmwareUpdate.setVersion(firmwareUpdateCreate.getVersion());
            update = true;
        }

        if (firmwareUpdateCreate.getFileResource() != null && (firmwareUpdate.getFileResource() == null || !firmwareUpdateCreate.getFileResource().getId().equals(firmwareUpdate.getFileResource().getId()))) {
            firmwareUpdate.setFileResource(firmwareUpdateCreate.getFileResource());
            update = true;
        }
        if(firmwareUpdateCreate.getFileResource()!=null){
            File file = new File(firmwareUpdateCreate.getFileResource().getFullPath());
            try {
                long crc = FileUtils.checksumCRC32(file);
                String crcS = Base64.getEncoder().encodeToString(BigInteger.valueOf(crc).toByteArray());
                if(!crcS.equals(firmwareUpdate.getCrc())){
                    firmwareUpdate.setCrc(crcS);
                    update=true;
                }

            } catch (IOException e) {
                logger.error("error calculating crc ",e);
            }
        }


        return update;
    }

    public FirmwareUpdate updateFirmwareUpdate(FirmwareUpdateUpdate firmwareUpdateUpdate,
                               SecurityContextBase securityContext) {
        FirmwareUpdate firmwareUpdate = firmwareUpdateUpdate.getFirmwareUpdate();
        if (updateFirmwareUpdateNoMerge(firmwareUpdate, firmwareUpdateUpdate)) {
            repository.merge(firmwareUpdate);
        }
        return firmwareUpdate;
    }



}