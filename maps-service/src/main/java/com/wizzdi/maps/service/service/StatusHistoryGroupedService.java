package com.wizzdi.maps.service.service;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.service.data.StatusHistoryGroupedRepository;
import com.wizzdi.maps.service.request.StatusHistoryForDateRequest;
import com.wizzdi.maps.service.request.StatusHistoryGroupedRequest;
import com.wizzdi.maps.service.response.StatusHistoryGroupedEntry;
import com.wizzdi.maps.service.response.StatusHistoryGroupedResponse;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component
public class StatusHistoryGroupedService implements Plugin {

    @Autowired
    private StatusHistoryGroupedRepository statusHistoryGroupedRepository;
    @Autowired
    private MappedPOIService mappedPOIService;

    public StatusHistoryGroupedResponse listAllStatusHistoriesGrouped(StatusHistoryGroupedRequest statusHistoryGroupedRequest, SecurityContextBase securityContextBase){
        Map<String, StatusHistory> lastEventsForMappedPOI = listLastEvents(new StatusHistoryForDateRequest(statusHistoryGroupedRequest), securityContextBase);

        Map<String,List<StatusHistory>> byStatus=lastEventsForMappedPOI.values().stream().collect(Collectors.groupingBy(f->f.getMapIcon().getId()));
        List<StatusHistoryGroupedEntry> entryList=new ArrayList<>();
        for ( List<StatusHistory> list : byStatus.values()) {
            MapIcon mapIcon=list.get(0).getMapIcon();
            entryList.add(new StatusHistoryGroupedEntry().setMapIcon(mapIcon).setCount(list.size()));
        }

        return new StatusHistoryGroupedResponse().setStatusHistoryGroupedEntries(entryList);

    }

    private Map<String, StatusHistory> listLastEvents(StatusHistoryForDateRequest statusHistoryGroupedRequest, SecurityContextBase securityContextBase) {
        Set<String> mappedPOIS = mappedPOIService.listAllMappedPOIs(statusHistoryGroupedRequest.getMappedPOIFilter(), securityContextBase).stream().map(f->f.getId()).collect(Collectors.toSet());

        return mappedPOIS.isEmpty()?new HashMap<>():statusHistoryGroupedRepository.listLastEventForMappedPOI(statusHistoryGroupedRequest, securityContextBase).stream().filter(f->mappedPOIS.contains(f.getMappedPOI().getId())).collect(Collectors.toMap(f->f.getMappedPOI().getId(), f->f));
    }

    public void validate(StatusHistoryGroupedRequest statusHistoryGroupedRequest, SecurityContextBase securityContext) {
        if(statusHistoryGroupedRequest.getStatusAtDate()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"statusAtDate must be provided");
        }
        if(statusHistoryGroupedRequest.getMappedPOIFilter()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"mappedPOIFilter must be provided");

        }
        mappedPOIService.validate(statusHistoryGroupedRequest.getMappedPOIFilter(), securityContext);
    }

    public PaginationResponse<StatusHistory> getAllStatusHistoriesForDate(StatusHistoryForDateRequest statusHistoryForDateRequest, SecurityContextBase securityContext) {
        List<StatusHistory> list=new ArrayList<>(listLastEvents(statusHistoryForDateRequest,securityContext).values());
        return new PaginationResponse<>(list,list.size(),list.size());
    }
}
