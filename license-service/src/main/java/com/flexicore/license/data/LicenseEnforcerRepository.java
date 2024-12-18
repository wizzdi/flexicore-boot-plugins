package com.flexicore.license.data;

import com.flexicore.license.request.ClazzCountRequest;
import com.flexicore.license.response.ClazzCount;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass_;
import com.flexicore.model.Clazz;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Extension
@Component
public class LicenseEnforcerRepository implements Plugin {

	@PersistenceContext
	private EntityManager em;


	public List<ClazzCount> getClazzCount(ClazzCountRequest clazzCountRequest) {
		/* TODO:use union query over clazz types since baseclass is mappedsuperclass
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClazzCount> q = cb.createQuery(ClazzCount.class);
		Root<Baseclass> r = q.from(Baseclass.class);
		Join<Baseclass, Clazz> join=r.join(Baseclass_.clazz);

		List<Predicate> preds=new ArrayList<>();
		preds.add(r.get(Baseclass_.tenant).isNotNull());
		preds.add(cb.isFalse(r.get(Baseclass_.softDelete)));
		Predicate[] predsArr=new Predicate[preds.size()];
		preds.toArray(predsArr);
		CriteriaQuery<ClazzCount> select;
		List<Expression<?>> groupBy;
		if(clazzCountRequest.isGroupByTenant()){
			select = q.select(cb.construct(ClazzCount.class,r.get(Baseclass_.tenant), join.get(Clazz_.name), cb.count(r.get(Baseclass_.id))));
			groupBy= Arrays.asList(r.get(Baseclass_.tenant),join.get(Clazz_.name));
		}
		else{
			select = q.select(cb.construct(ClazzCount.class, join.get(Clazz_.name), cb.count(r.get(Baseclass_.id))));
			groupBy= Arrays.asList(join.get(Clazz_.name));


		}
		select.where(predsArr)
				.groupBy(groupBy)
				.orderBy(cb.asc(join.get(Clazz_.name)));
		return em.createQuery(q).getResultList();
*/
		return Collections.emptyList();
	}
}
