package com.flexicore.territories.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.City_;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.CityFiltering;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@PluginInfo(version = 1)
public class CityRepository extends AbstractRepositoryPlugin {


    public List<City> listAllCities(SecurityContext securityContext, CityFiltering filtering) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<City> q=cb.createQuery(City.class);
        Root<City> r=q.from(City.class);
        List<Predicate> preds=new ArrayList<>();
        addCityPredicate(filtering,cb,r,preds);
        QueryInformationHolder<City> queryInformationHolder=new QueryInformationHolder<>(filtering,City.class,securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);

    }


    public long countAllCities(SecurityContext securityContext, CityFiltering filtering) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<Long> q=cb.createQuery(Long.class);
        Root<City> r=q.from(City.class);
        List<Predicate> preds=new ArrayList<>();
        addCityPredicate(filtering,cb,r,preds);
        QueryInformationHolder<City> queryInformationHolder=new QueryInformationHolder<>(filtering,City.class,securityContext);
        return countAllFiltered(queryInformationHolder,preds,cb,q,r);

    }


    private void addCityPredicate(CityFiltering filtering, CriteriaBuilder cb, Root<City> r, List<Predicate> preds) {
        if(filtering.getCountry()!=null ){
            preds.add(cb.equal(r.get(City_.country),filtering.getCountry()));
        }
        if(filtering.getState()!=null ){
            preds.add(cb.equal(r.get(City_.state),filtering.getState()));
        }
    }
}