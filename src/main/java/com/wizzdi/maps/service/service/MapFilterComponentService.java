package com.wizzdi.maps.service.service;

import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.service.data.MapFilterComponentRepository;
import com.wizzdi.maps.service.request.MapFilterComponentRequest;
import com.wizzdi.maps.service.response.MapFilterComponent;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Extension
@Component
public class MapFilterComponentService implements Plugin {
    @Autowired
    private MapFilterComponentRepository mapFilterComponentRepository;
    @Autowired
    private MappedPOIService mappedPOIService;
    private static final Logger logger= LoggerFactory.getLogger(MappedPOIRelatedService.class);

    public void validate(MapFilterComponentRequest mapFilterComponentRequest, SecurityContextBase securityContextBase){
        if(mapFilterComponentRequest.getFilterComponentType()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"filter component type must be provided");
        }
        if(mapFilterComponentRequest.getMappedPOIFilter()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"mapped poi filter must be provided");
        }
        mappedPOIService.validate(mapFilterComponentRequest.getMappedPOIFilter(),securityContextBase);
    }



    public PaginationResponse<MapFilterComponent> getAllMapFilterComponents(MapFilterComponentRequest mapFilterComponentRequest, SecurityContextBase securityContext) {
        List<Tuple> list=mapFilterComponentRepository.listAllMapFilterComponents(mapFilterComponentRequest,securityContext);
        long count=mapFilterComponentRepository.countAllMapFilterComponents(mapFilterComponentRequest,securityContext);
        List<MapFilterComponent> converted=list.stream().map(this::toMapFilterComponent).filter(Objects::nonNull).collect(Collectors.toList());
        return new PaginationResponse<>(converted,mapFilterComponentRequest,count);

    }

    private MapFilterComponent toMapFilterComponent(Tuple f) {

        Object o = f.get(0);
        Long count = f.get(1, Long.class);
        MapFilterComponent mapFilterComponent=new MapFilterComponent()
                .setCount(count);
        if(o instanceof Basic){
            Basic basic= (Basic) o;
            return mapFilterComponent.setId(basic.getId())
                    .setName(mapFilterComponent.getName());
        }
        if(o instanceof String){
            String s= (String) o;
            return mapFilterComponent.setName(s).setId(s);
        }
        logger.warn("could not get map filter from tuple:"+f);
        return null;
    }

}
