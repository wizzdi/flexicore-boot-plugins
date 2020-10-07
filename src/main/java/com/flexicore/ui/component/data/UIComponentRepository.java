package com.flexicore.ui.component.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.component.model.UIComponent;
import com.flexicore.ui.component.model.UIComponent_;
import org.springframework.stereotype.Component;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//

/**
 * Created by Asaf on 12/07/2017.
 */
@Component
@PluginInfo(version = 1)
public class UIComponentRepository extends AbstractRepositoryPlugin implements ServicePlugin {

    public List<UIComponent> getExistingUIComponentsByIds(Set<String> ids) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UIComponent> q = cb.createQuery(UIComponent.class);
        Root<UIComponent> r = q.from(UIComponent.class);
        Predicate p = r.get(UIComponent_.externalId).in(ids);
        q.select(r).where(p);
        TypedQuery<UIComponent> query=em.createQuery(q);
        return query.getResultList();
    }

    public List<UIComponent> getAllowedUIComponentsByIds(Set<String> ids, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UIComponent> q = cb.createQuery(UIComponent.class);
        Root<UIComponent> r = q.from(UIComponent.class);
        Predicate p = r.get(UIComponent_.id).in(ids);
        List<Predicate> preds= new ArrayList<>();
        preds.add(p);
        QueryInformationHolder<UIComponent> queryInformationHolder= new QueryInformationHolder<>(UIComponent.class,securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);

    }



}
