package com.wizzdi.maps.service;

import com.flexicore.model.Basic_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.request.FilterComponentPropertyProvider;
import com.wizzdi.maps.service.request.MapFilterComponentRequest;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import com.wizzdi.maps.service.request.PredicateAdder;
import com.wizzdi.maps.service.response.GeoHashResponse;
import com.wizzdi.maps.service.response.MapFilterComponent;
import com.wizzdi.maps.service.service.GeoHashService;
import com.wizzdi.maps.service.service.MapFilterComponentService;
import com.wizzdi.maps.service.service.MappedPOIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.*;
import java.util.List;

@Component
public class LightMappedPOIService {

    @Autowired
    private MappedPOIService mappedPOIService;
    @Autowired
    private MapFilterComponentService mapFilterComponentService;
    @Autowired
    private GeoHashService geoHashService;
    @Autowired
    private LightRepository lightRepository;

    public void validate(LightMapFilterComponentRequest mapFilterComponentRequest, SecurityContextBase securityContext) {
        mapFilterComponentRequest.setCustom(true);
        mapFilterComponentService.validate(mapFilterComponentRequest,securityContext);
        mapFilterComponentRequest.setFilterComponentPropertyProvider(new FilterComponentPropertyProvider() {
            @Override
            public Path<?> getPropertyPath(Root<?> r,CriteriaBuilder cb, CriteriaQuery<?> q, List<Predicate> predicates, MapFilterComponentRequest filter) {
                Root<Light> lightR = q.from(Light.class);
                Join<Light, LightOperator> lightOperator = lightR.join("lightOperator");
                predicates.add(cb.equal(r.get("relatedId"),lightR.get(Basic_.id)));
                if(filter instanceof LightMapFilterComponentRequest){
                    lightRepository.addLightPredicate(((LightMapFilterComponentRequest) filter).getLightFilter(),cb,q,lightR,predicates,securityContext);

                }

                return lightOperator;
            }

            @Override
            public Class<?> getType(MapFilterComponentRequest filter) {
                return LightOperator.class;
            }
        });

    }

    public void validate(ExtendedMappedPOIFilter mapFilterComponentRequest, SecurityContextBase securityContext) {
        mappedPOIService.validate(mapFilterComponentRequest,securityContext);

        mapFilterComponentRequest.setPredicateAdder(new PredicateAdder<>() {
            @Override
            public <T extends MappedPOI> void addPredicates(MappedPOIFilter mapFilterComponentRequest, CriteriaBuilder cb, CriteriaQuery<?> q, From<?, T> r, List<Predicate> preds, SecurityContextBase securityContext) {
                if(mapFilterComponentRequest instanceof ExtendedMappedPOIFilter){
                    Root<Light> lightR =  q.from(Light.class);
                    ExtendedMappedPOIFilter extendedMappedPOIFilter= (ExtendedMappedPOIFilter) mapFilterComponentRequest;
                    preds.add(cb.equal(r.get("relatedId"),lightR.get(Basic_.id)));
                    lightRepository.addLightPredicate(extendedMappedPOIFilter.getLightFilter(),cb,q,lightR,preds,securityContext);

                }
            }
        });
    }

    public void validate(ExtendedGeoHashRequest extendedGeoHashRequest, SecurityContextBase securityContext) {
        geoHashService.validate(extendedGeoHashRequest,securityContext);

        extendedGeoHashRequest.getMappedPOIFilter().setPredicateAdder(new PredicateAdder<>() {
            @Override
            public <T extends MappedPOI> void addPredicates(MappedPOIFilter mapFilterComponentRequest, CriteriaBuilder cb, CriteriaQuery<?> q, From<?, T> r, List<Predicate> preds, SecurityContextBase securityContext) {
                if(mapFilterComponentRequest instanceof ExtendedMappedPOIFilter){
                    Root<Light> lightR =  q.from(Light.class);
                    ExtendedMappedPOIFilter extendedMappedPOIFilter= (ExtendedMappedPOIFilter) mapFilterComponentRequest;
                    preds.add(cb.equal(r.get("relatedId"),lightR.get(Basic_.id)));
                    lightRepository.addLightPredicate(extendedMappedPOIFilter.getLightFilter(),cb,q,lightR,preds,securityContext);

                }
            }
        });
    }

    public PaginationResponse<MapFilterComponent> getAllMapFilterComponents(LightMapFilterComponentRequest mapFilterComponentRequest, SecurityContextBase securityContext) {
        return mapFilterComponentService.getAllMapFilterComponents(mapFilterComponentRequest,securityContext);
    }

    public PaginationResponse<MappedPOI> getAllMappedPOIs(ExtendedMappedPOIFilter mapFilterComponentRequest, SecurityContextBase securityContext) {
        return mappedPOIService.getAllMappedPOIs(mapFilterComponentRequest,securityContext);
    }

    public PaginationResponse<GeoHashResponse> getAllGeoHashAreas(ExtendedGeoHashRequest extendedGeoHashRequest, SecurityContextBase securityContext) {
        return geoHashService.getAllGeoHashAreas(extendedGeoHashRequest,securityContext);
    }
}
