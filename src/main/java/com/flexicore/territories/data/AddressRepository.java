package com.flexicore.territories.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.Address;
import com.flexicore.model.territories.Address_;
import com.flexicore.model.territories.Street;
import com.flexicore.model.territories.Street_;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.AddressFiltering;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class AddressRepository extends AbstractRepositoryPlugin {

    public List<Address> getAllAddresses(SecurityContext securityContext, AddressFiltering filtering) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<Address> q=cb.createQuery(Address.class);
        Root<Address> r=q.from(Address.class);
        List<Predicate> preds=new ArrayList<>();
        addAddressPredicate(filtering,cb,r,preds);
        QueryInformationHolder<Address> queryInformationHolder=new QueryInformationHolder<>(filtering,Address.class,securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);

    }


    public long countAllAddresses(SecurityContext securityContext, AddressFiltering filtering) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<Long> q=cb.createQuery(Long.class);
        Root<Address> r=q.from(Address.class);
        List<Predicate> preds=new ArrayList<>();
        addAddressPredicate(filtering,cb,r,preds);
        QueryInformationHolder<Address> queryInformationHolder=new QueryInformationHolder<>(filtering,Address.class,securityContext);
        return countAllFiltered(queryInformationHolder,preds,cb,q,r);

    }


    private void addAddressPredicate(AddressFiltering filtering, CriteriaBuilder cb, Root<Address> r, List<Predicate> preds) {
        if(filtering.getFloors()!=null && !filtering.getFloors().isEmpty() ){
            preds.add(r.get(Address_.floorForAddress).in(filtering.getFloors()));
        }
        if(filtering.getNumbers()!=null && !filtering.getNumbers().isEmpty() ){
            preds.add(r.get(Address_.number).in(filtering.getNumbers()));
        }
        if(filtering.getZipCodes()!=null && !filtering.getZipCodes().isEmpty() ){
            preds.add(r.get(Address_.zipCode).in(filtering.getZipCodes()));
        }
        if(filtering.getStreets()!=null && !filtering.getStreets().isEmpty() ){
            Set<String> ids=filtering.getStreets().parallelStream().map(f->f.getId()).collect(Collectors.toSet());
            Join<Address, Street> join=r.join(Address_.street);
            preds.add(join.get(Street_.id).in(ids));
        }
    }
}