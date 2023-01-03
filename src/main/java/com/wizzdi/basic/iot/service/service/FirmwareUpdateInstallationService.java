package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.FirmwareInstallationState;
import com.wizzdi.basic.iot.model.FirmwareUpdateInstallation;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.data.FirmwareUpdateInstallationRepository;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateInstallationCreate;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateInstallationFilter;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateInstallationMassCreate;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateInstallationUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.SingularAttribute;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Extension
@Component

public class FirmwareUpdateInstallationService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(FirmwareUpdateInstallationService.class);

    @Autowired
    private FirmwareUpdateInstallationRepository repository;

    @Autowired
    private BasicService basicService;
    @Autowired
    private RemoteService remoteService;

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

    public PaginationResponse<FirmwareUpdateInstallation> getAllFirmwareUpdateInstallations(
            SecurityContextBase securityContext, FirmwareUpdateInstallationFilter filtering) {
        List<FirmwareUpdateInstallation> list = listAllFirmwareUpdateInstallations(securityContext, filtering);
        long count = repository.countAllFirmwareUpdateInstallations(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<FirmwareUpdateInstallation> listAllFirmwareUpdateInstallations(SecurityContextBase securityContext, FirmwareUpdateInstallationFilter firmwareUpdateInstallationFilter) {
        return repository.getAllFirmwareUpdateInstallations(securityContext, firmwareUpdateInstallationFilter);
    }

    public FirmwareUpdateInstallation createFirmwareUpdateInstallation(FirmwareUpdateInstallationCreate creationContainer,
                               SecurityContextBase securityContext) {
        FirmwareUpdateInstallation firmwareUpdateInstallation = createFirmwareUpdateInstallationNoMerge(creationContainer, securityContext);
        repository.merge(firmwareUpdateInstallation);
        return firmwareUpdateInstallation;
    }

    public FirmwareUpdateInstallation createFirmwareUpdateInstallationNoMerge(FirmwareUpdateInstallationCreate creationContainer,
                                      SecurityContextBase securityContext) {
        FirmwareUpdateInstallation firmwareUpdateInstallation = new FirmwareUpdateInstallation();
        firmwareUpdateInstallation.setId(UUID.randomUUID().toString());

        updateFirmwareUpdateInstallationNoMerge(firmwareUpdateInstallation, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(firmwareUpdateInstallation, securityContext);
        return firmwareUpdateInstallation;
    }

    public boolean updateFirmwareUpdateInstallationNoMerge(FirmwareUpdateInstallation firmwareUpdateInstallation,
                                       FirmwareUpdateInstallationCreate firmwareUpdateInstallationCreate) {
        if(firmwareUpdateInstallationCreate.getTargetInstallationDate()==null&&firmwareUpdateInstallation.getTargetInstallationDate()==null){
            firmwareUpdateInstallationCreate.setTargetInstallationDate(OffsetDateTime.now());
        }
        if(firmwareUpdateInstallationCreate.getNextTimeForReminder()==null&&firmwareUpdateInstallation.getNextTimeForReminder()==null){
            firmwareUpdateInstallationCreate.setNextTimeForReminder(firmwareUpdateInstallationCreate.getTargetInstallationDate());
        }
        if(firmwareUpdateInstallationCreate.getFirmwareInstallationState()==null&&firmwareUpdateInstallation.getFirmwareInstallationState()==null){
            firmwareUpdateInstallationCreate.setFirmwareInstallationState(FirmwareInstallationState.PENDING);
        }
        boolean update = basicService.updateBasicNoMerge(firmwareUpdateInstallationCreate, firmwareUpdateInstallation);
        if (firmwareUpdateInstallationCreate.getFirmwareUpdate() != null && !firmwareUpdateInstallationCreate.getFirmwareUpdate().equals(firmwareUpdateInstallation.getFirmwareUpdate())) {
            firmwareUpdateInstallation.setFirmwareUpdate(firmwareUpdateInstallationCreate.getFirmwareUpdate());
            update = true;
        }

        if (firmwareUpdateInstallationCreate.getTargetRemote() != null && (firmwareUpdateInstallation.getTargetRemote() == null || !firmwareUpdateInstallationCreate.getTargetRemote().getId().equals(firmwareUpdateInstallation.getTargetRemote().getId()))) {
            firmwareUpdateInstallation.setTargetRemote(firmwareUpdateInstallationCreate.getTargetRemote());
            update = true;
        }
        if (firmwareUpdateInstallationCreate.getDateInstalled() != null &&!firmwareUpdateInstallationCreate.getDateInstalled().equals(firmwareUpdateInstallation.getDateInstalled())) {
            firmwareUpdateInstallation.setDateInstalled(firmwareUpdateInstallationCreate.getDateInstalled());
            update = true;
        }

        if (firmwareUpdateInstallationCreate.getTargetInstallationDate() != null &&!firmwareUpdateInstallationCreate.getTargetInstallationDate().equals(firmwareUpdateInstallation.getTargetInstallationDate())) {
            firmwareUpdateInstallation.setTargetInstallationDate(firmwareUpdateInstallationCreate.getTargetInstallationDate());
            update = true;
        }
        if (firmwareUpdateInstallationCreate.getNextTimeForReminder() != null &&!firmwareUpdateInstallationCreate.getNextTimeForReminder().equals(firmwareUpdateInstallation.getNextTimeForReminder())) {
            firmwareUpdateInstallation.setNextTimeForReminder(firmwareUpdateInstallationCreate.getNextTimeForReminder());
            update = true;
        }
        if (firmwareUpdateInstallationCreate.getFirmwareInstallationState() != null &&!firmwareUpdateInstallationCreate.getFirmwareInstallationState().equals(firmwareUpdateInstallation.getFirmwareInstallationState())) {
            firmwareUpdateInstallation.setFirmwareInstallationState(firmwareUpdateInstallationCreate.getFirmwareInstallationState());
            update = true;
        }



        return update;
    }

    public FirmwareUpdateInstallation updateFirmwareUpdateInstallation(FirmwareUpdateInstallationUpdate firmwareUpdateInstallationUpdate,
                               SecurityContextBase securityContext) {
        FirmwareUpdateInstallation firmwareUpdateInstallation = firmwareUpdateInstallationUpdate.getFirmwareUpdateInstallation();
        if (updateFirmwareUpdateInstallationNoMerge(firmwareUpdateInstallation, firmwareUpdateInstallationUpdate)) {
            repository.merge(firmwareUpdateInstallation);
        }
        return firmwareUpdateInstallation;
    }


    public List<FirmwareUpdateInstallation> massCreateFirmwareUpdateInstallation(FirmwareUpdateInstallationMassCreate firmwareUpdateInstallationMassCreate, SecurityContextBase securityContext) {
        List<Remote> remotes = remoteService.listAllRemotes(securityContext, firmwareUpdateInstallationMassCreate.getRemoteFilter());
        return remotes.stream().map(f->createFirmwareUpdateInstallation(new FirmwareUpdateInstallationCreate()
                .setTargetRemote(f)
                .setFirmwareUpdate(firmwareUpdateInstallationMassCreate.getFirmwareUpdate())
                .setTargetInstallationDate(firmwareUpdateInstallationMassCreate.getTargetInstallationDate()),securityContext)).collect(Collectors.toList());

    }
}