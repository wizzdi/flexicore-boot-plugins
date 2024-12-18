package com.wizzdi.basic.iot.service.data;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.StateHistory;
import com.wizzdi.basic.iot.model.StateHistory_;
import com.wizzdi.basic.iot.service.request.StateHistoryAggRequest;
import com.wizzdi.basic.iot.service.request.StateHistoryAggTimeUnit;
import com.wizzdi.basic.iot.service.response.StateHistoryAggEntry;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.io.IOUtils;
import org.pf4j.Extension;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Extension
public class StateHistoryAggRepository implements Plugin, InitializingBean {

    @Autowired
    private StateHistoryRepository stateHistoryRepository;
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource dataSource;

    public List<StateHistoryAggEntry> listAllStateHistoriesAgg(StateHistoryAggRequest stateHistoryAggRequest, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createQuery(Tuple.class);
        Root<StateHistory> r = q.from(StateHistory.class);
        List<Predicate> preds = new ArrayList<>();
        stateHistoryRepository.addStateHistoryPredicates(stateHistoryAggRequest.getStateHistoryFilter(), cb, q, r, preds, securityContext);
        List<Expression<String>> pathExprs = Arrays.stream(stateHistoryAggRequest.getGroupByFieldName().split("\\.")).map(f -> cb.literal(f)).toList();
        List<Expression<?>> exps=new ArrayList<>();
        exps.add(r.get(StateHistory_.deviceProperties));
        exps.addAll(pathExprs);
        Expression<Number> dataPath = cb.function("jsonb_extract_path_as_numeric", Number.class, exps.toArray(Expression[]::new));

        String functionName = getFunctionName(stateHistoryAggRequest.getTimeUnit());

        Expression<OffsetDateTime> dateTruncGroup = cb.function(functionName,
                OffsetDateTime.class, r.get(StateHistory_.timeAtState));
        List<Selection<?>> selections=new ArrayList<>(List.of(dateTruncGroup,cb.sum(dataPath)));
        List<Expression<?>> groupBy=new ArrayList<>(List.of(dateTruncGroup));
        if(stateHistoryAggRequest.isGroupByRemote()){
            selections.add(r.get(StateHistory_.remote));
            groupBy.add(r.get(StateHistory_.remote));
        }
        q.select(cb.tuple(selections.toArray(Selection[]::new))).where(preds.toArray(Predicate[]::new))
                .groupBy(groupBy)
                .orderBy(cb.asc(dateTruncGroup));
        TypedQuery<Tuple> query = em.createQuery(q);
        BasicRepository.addPagination(stateHistoryAggRequest, query);
        return toStateHistoryAggEntry(query.getResultList());

    }

    private List<StateHistoryAggEntry> toStateHistoryAggEntry(List<Tuple> resultList) {
        List<StateHistoryAggEntry> ret = new ArrayList<>();
        for (Tuple o : resultList) {

            ret.add(new StateHistoryAggEntry(o));
        }
        return ret;
    }




    private String getFunctionName(StateHistoryAggTimeUnit timeUnit) {
        return switch (timeUnit) {
            case MINUTES -> "trunc_minute";
            case DAYS -> "trunc_day";
            case HOURS -> "trunc_hour";
            case MONTHS -> "trunc_month";
            case YEARS -> "trunc_year";
            case WEEKS -> "trunc_week";
        };
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //run functions.sql from resources

        createSQLFunctions();

    }

    private void createSQLFunctions() throws IOException, SQLException {
        String functionsSQL = IOUtils.resourceToString("functions.sql", StandardCharsets.UTF_8, getClass().getClassLoader());
        //execute functionsSQL using jdbc

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(functionsSQL);
        }

    }
}
