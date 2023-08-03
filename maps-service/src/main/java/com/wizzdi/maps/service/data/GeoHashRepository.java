package com.wizzdi.maps.service.data;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.MappedPOI_;
import com.wizzdi.maps.service.request.GeoHashRequest;
import com.wizzdi.maps.service.response.GeoHashResponse;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Extension
@Component
public class GeoHashRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private MappedPOIRepository mappedPOIRepository;


    public List<GeoHashResponse> listAllGeoHashAreas(GeoHashRequest geoHashRequest, SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GeoHashResponse> q = cb.createQuery(GeoHashResponse.class);
        Root<MappedPOI> r = q.from(MappedPOI.class);
        List<Predicate> preds = new ArrayList<>();
        String geoHashField="geoHash"+geoHashRequest.getPrecision();
        addGeoHashAreasPredicate(geoHashRequest, cb, q, r, preds, securityContext);
        q.select(cb.construct(GeoHashResponse.class,r.get(geoHashField),cb.count(r))).where(preds.toArray(new Predicate[0])).groupBy(r.get(geoHashField)).orderBy(cb.desc(r.get(geoHashField)));
        TypedQuery<GeoHashResponse> query = em.createQuery(q);
        return query.getResultList();
    }

    private void addGeoHashAreasPredicate(GeoHashRequest geoHashRequest, CriteriaBuilder cb, CriteriaQuery<GeoHashResponse> q, From<?,MappedPOI> r, List<Predicate> preds, SecurityContextBase securityContext) {
        mappedPOIRepository.addMappedPOIPredicate(geoHashRequest.getMappedPOIFilter(),cb,q,r,preds,securityContext);
        String geoHashField="geoHash"+geoHashRequest.getPrecision();

        preds.add(r.get(geoHashField).isNotNull());

    }

}
