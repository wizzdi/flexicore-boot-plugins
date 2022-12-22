package com.wizzdi.maps.service.data;

import com.flexicore.model.SecurityTenant;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.service.request.StatusHistoryForDateRequest;
import com.wizzdi.maps.service.request.StatusHistoryGroupedRequest;
import com.wizzdi.maps.service.response.StatusHistoryGroupedEntry;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Extension
@Component
public class StatusHistoryGroupedRepository implements Plugin {
  @PersistenceContext private EntityManager em;


  /**
   * @param statusHistoryGroupedRequest Object Used to List StatusHistory
   * @param securityContext
   * @return List of StatusHistory
   */
  public List<StatusHistory> listLastEventForMappedPOI(
          StatusHistoryForDateRequest statusHistoryGroupedRequest, SecurityContextBase securityContext) {
    List<? extends SecurityTenant> tenants = securityContext.getTenants();
    List<String> collect = new ArrayList<>(tenants.stream().map(f -> f.getId()).collect(Collectors.toSet()));
    List<String> listPlaceHolders=new ArrayList<>();
    for (int i = 0; i < collect.size(); i++) {
      listPlaceHolders.add("?"+(i+2));

    }

    String listPlaceHolder=listPlaceHolders.stream().collect(Collectors.joining(","));

    OffsetDateTime statusAtDate = statusHistoryGroupedRequest.getStatusAtDate();
    Query nativeQuery = em.createNativeQuery(
            "select h.* from StatusHistory as h inner join (" +
            "select max(s.dateAtStatus) as maxDateAtStatus,s.mappedPOI_id from StatusHistory as s join MappedPOI as m on m.id=s.mappedPOI_id join Baseclass as b on b.id=m.security_id where s.dateAtStatus <= ?1 and b.tenant_id in ("+listPlaceHolder+") group by s.mappedPOI_id " +
            ") groupedStatus on h.dateAtStatus=groupedStatus.maxDateAtStatus and h.mappedPOI_id=groupedStatus.mappedPOI_id",StatusHistory.class);
    nativeQuery.setParameter(1, statusAtDate);
    for (int i = 0; i < collect.size(); i++) {
      nativeQuery.setParameter(i+2,collect.get(i));

    }


    List<StatusHistory> resultList = nativeQuery.getResultList();
    return resultList;

  }

}
