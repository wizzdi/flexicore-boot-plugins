package com.wizzdi.maps.service.service;

import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.data.MappedPOIRelatedRepository;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import com.wizzdi.maps.service.response.MappedPOIRelated;
import com.wizzdi.maps.service.service.MappedPOIService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Extension
public class MappedPOIRelatedService implements Plugin {
    private static final Logger logger= LoggerFactory.getLogger(MappedPOIRelatedService.class);
    @Autowired
    private MappedPOIService mappedPOIService;
    @Autowired
    private MappedPOIRelatedRepository mappedPOIRelatedRepository;


    public void validate(MappedPOIFilter mappedPOIFilter, SecurityContext securityContext) {
        mappedPOIService.validate(mappedPOIFilter,securityContext);
    }

    public PaginationResponse<MappedPOIRelated> getAllMappedPOIRelated(MappedPOIFilter mappedPOIFilter, SecurityContext securityContext) {
        Map<String,Basic> related=new HashMap<>();
        PaginationResponse<MappedPOI> paginationResponse = mappedPOIService.getAllMappedPOIs(mappedPOIFilter, securityContext);
        List<MappedPOI> mappedPOIS = paginationResponse.getList();
        Map<String, Set<String>> collect = mappedPOIS.stream().filter(f -> f.getRelatedId() != null && f.getRelatedType() != null).collect(Collectors.groupingBy(f -> f.getRelatedType(),Collectors.mapping(f->f.getRelatedId(),Collectors.toSet())));
        for (Map.Entry<String, Set<String>> stringSetEntry : collect.entrySet()) {
            try {
                Class<? extends Basic> type= (Class<? extends Basic>) Class.forName(stringSetEntry.getKey());
                List<? extends Basic > relatedForType = mappedPOIRelatedRepository.getRelatedForType(type, stringSetEntry.getValue());
                related.putAll(relatedForType.stream().collect(Collectors.toMap(f->f.getId(),f->f,(a,b)->a)));
            } catch (ClassNotFoundException e) {
                logger.error("could not find "+stringSetEntry.getKey(),e);
            }

        }
        List<MappedPOIRelated> list=mappedPOIS.stream().map(f->new MappedPOIRelated(f,related.get(f.getRelatedId()))).collect(Collectors.toList());
        return new PaginationResponse<>(list,mappedPOIFilter.getPageSize(),paginationResponse.getTotalRecords());
    }

}
