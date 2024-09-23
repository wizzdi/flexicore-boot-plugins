package com.wizzdi.maps.service.service;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.maps.service.data.BuildingFloorRepository;
import com.wizzdi.maps.service.data.BuildingRepository;
import com.wizzdi.maps.service.data.MappedPOIRepository;
import com.wizzdi.maps.service.data.RoomRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Extension
public class IndexCreator implements Plugin, InitializingBean {
    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private BuildingFloorRepository buildingFloorRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private MappedPOIRepository mappedPOIRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        buildingRepository.createBuildingIdx();
        buildingFloorRepository.createBuildingFloorIdx();
        roomRepository.createRoomIdx();
        mappedPOIRepository.createMappedPOIIdx();
    }
}
