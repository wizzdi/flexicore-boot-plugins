package com.wizzdi.maps.service.service;

import com.flexicore.model.territories.Address;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.data.GeoHashRepository;
import com.wizzdi.maps.service.request.GeoHashRequest;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import com.wizzdi.maps.service.request.MappedPOIUpdate;
import com.wizzdi.maps.service.response.GeoHashResponse;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@Extension
public class GeoHashService implements Plugin {

    @Autowired
    private MappedPOIService mappedPOIService;
    @Autowired
    private GeoHashRepository geoHashRepository;
    @Autowired
    private ReverseGeoHashService reverseGeoHashService;

    public void validate(GeoHashRequest geoHashRequest, SecurityContextBase securityContext) {
        if(geoHashRequest.getPrecision()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"precision must be provided");
        }
        if(geoHashRequest.getPrecision() <1 || geoHashRequest.getPrecision()>12){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"precision must be between 1 and 12");

        }
        if(geoHashRequest.getMappedPOIFilter()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"mappedPOIFilter must be provided");
        }
        mappedPOIService.validate(geoHashRequest.getMappedPOIFilter(),securityContext);
    }

    public PaginationResponse<GeoHashResponse> getAllGeoHashAreas(GeoHashRequest geoHashRequest, SecurityContextBase securityContext) {
        List<GeoHashResponse> list=listAllGeoHashAreas(geoHashRequest,securityContext);
        long count=list.size();
        return new PaginationResponse<>(list,list.size(),count);
    }

    private List<GeoHashResponse> listAllGeoHashAreas(GeoHashRequest geoHashRequest, SecurityContextBase securityContext) {
        return geoHashRepository.listAllGeoHashAreas(geoHashRequest,securityContext);
    }

    public void calculateReverseGeoHash(MappedPOIFilter mappedPOIFilter, SecurityContextBase securityContext) {
        List<MappedPOI> mappedPOIS = mappedPOIService.listAllMappedPOIs(mappedPOIFilter, securityContext);
        for (MappedPOI pois : mappedPOIS) {
            if(pois.getLat()!=null&&pois.getLon()!=null){
                Address address = reverseGeoHashService.getAddress(pois.getLat(), pois.getLon(), securityContext);
                mappedPOIService.updateMappedPOI(new MappedPOIUpdate().setMappedPOI(pois).setAddress(address),securityContext);

            }
        }
    }
}
